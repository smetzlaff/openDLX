/*******************************************************************************
 * openDLX - A DLX/MIPS processor simulator.
 * Copyright (C) 2013 The openDLX project, University of Augsburg, Germany
 * Project URL: <https://sourceforge.net/projects/opendlx>
 * Development branch: <https://github.com/smetzlaff/openDLX>
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, see <LICENSE>. If not, see
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package openDLX.branchPrediction;

import org.apache.log4j.Logger;
import openDLX.datatypes.BranchPredictorState;
import openDLX.datatypes.BranchPredictorType;
import openDLX.datatypes.BranchTargetBufferLookupResult;
import openDLX.datatypes.uint32;
import openDLX.exception.BranchPredictionException;

/**
 * Direct-mapped branch target buffer
 */
public class BranchTargetBuffer
{
	/// Logging facility
	private static Logger logger = Logger.getLogger("BTB");
	/// Number of entries of the branch target buffer
	private int btb_size;
	/// Configuration that defines the behavior on overwriting a branch target buffer entry by another branch (if true, which is not recommended, the predictor is reset to the initial state on branch target buffer miss)
	private boolean reset_predictor_on_overwrite;
	/// The type of the used branch predictor
	private BranchPredictorType predictor_type;
	/// The branch predictors initial state (on startup)
	private BranchPredictorState predictor_initial_state;
	/// Determines if a branch target buffer entry was already used and thus contains a branch address, branch target, and a prediction (encoded by the predictors internal state)
	private boolean branch_entry_valid[];
	/// Holds the branch address (the pc of the branch instruction) for each branch target buffer entry (if branch_entry_valid is false the entry was not used yet and thus is invalid)
	private uint32 branch_addresses[];
	/// Contains the branch target address of each branch target buffer entry (each branch has at least one branch target, which is calculated by the ALU during the execution stage) 
	/// Register-indirect jumps may jump to different addresses (like for a returning jump at the end of the function), thus on branch prediction the the predicted target has also be taken into account to decide if a branch was correctly predicted.  
	private uint32 branch_targets[];
	/// The branch predictors per entry of the branch target buffer
	private BranchPredictor branch_predictors[];
	
	private final boolean throwExceptionForUntestedFeatures = true;

	/**
	 * Constructor
	 * @param size Number of entries of the branch target buffer
	 * @param bp_type Type of the used predictors
	 * @param bp_init_state Initial state of the predictors
	 * @param resetPredictorOnOverwrite Boolean that defines the behavior on overwriting a branch target buffer entry by another branch (if true, which is not recommended, the predictor is reset to the initial state on branch target buffer miss)
	 * @throws BranchPredictionException 
	 */
	public BranchTargetBuffer(int size, BranchPredictorType bp_type, BranchPredictorState bp_init_state, boolean resetPredictorOnOverwrite) throws BranchPredictionException
	{
		// enforce that the BTB size is a power of two
		if(Integer.bitCount(size)!=1)
		{
			throw new BranchPredictionException("The BTB size has to be a power of two!");
		}
		
		if(bp_type == BranchPredictorType.UNKNOWN)
		{
			throw new BranchPredictionException("Unknown branch prediction type: " + bp_type);
		}
		
		btb_size = size;
		reset_predictor_on_overwrite = resetPredictorOnOverwrite;
		predictor_type = bp_type;
		predictor_initial_state = bp_init_state;
		
		initialize();
	}
		
	/**
	 * Initializes the branch target buffer.
	 * Creates arrays and initializes branch predictors. 
	 * @throws BranchPredictionException 
	 */
	private void initialize() throws BranchPredictionException
	{
		branch_entry_valid = new boolean[btb_size];
		branch_addresses = new uint32[btb_size];
		branch_targets = new uint32[btb_size];
		branch_predictors = new BranchPredictor[btb_size];
		
		logger.info("Initialising the predictors with: " + predictor_initial_state);
		
		for(int i = 0; i < btb_size; i++)
		{
			branch_entry_valid[i] = false;
			branch_addresses[i] = new uint32(0);
			branch_targets[i] = new uint32(0);
			switch(predictor_type)
			{
			case S_ALWAYS_NOT_TAKEN:
				branch_predictors[i] = new StaticBranchPredictor(false);
				break;
			case S_ALWAYS_TAKEN:
				branch_predictors[i] = new StaticBranchPredictor(true);
				break;
			case S_BACKWARD_TAKEN:
				logger.error("Branch predictor S_BACKWARD_TAKEN currently not implemented.");
				if(throwExceptionForUntestedFeatures)
				{
					throw new BranchPredictionException("Branch predictor S_BACKWARD_TAKEN currently not implemented.");
				}
				break;
			case D_1BIT:
				branch_predictors[i] = new OneBitBranchPredictor(predictor_initial_state);
				break;
			case D_2BIT_SATURATION:
				branch_predictors[i] = new TwoBitBranchPredictorSaturation(predictor_initial_state);
				break;
			case D_2BIT_HYSTERESIS:
				branch_predictors[i] = new TwoBitBranchPredictorHysteresis(predictor_initial_state);
				break;
			default:
				logger.error("Unsupported type of branch predictor.");
			}
		}
	}

	/**
	 * Updates the branch target buffer after calculation of the branch address by the ALU. 
	 * @param branch_pc The program counter of the branch instruction 
	 * @param branch_tgt The calculated target address of the branch
	 * @param taken True if the BranchControl module decided that the branch is taken, false otherwise
	 * @throws BranchPredictionException 
	 */
	public void updateOnBranch(uint32 branch_pc, uint32 branch_tgt, boolean taken) throws BranchPredictionException
	{
		int branch_idx = getIndexForBranchPc(branch_pc);
		
		if((branch_entry_valid[branch_idx]) && (branch_addresses[branch_idx].getValue() == branch_pc.getValue()) && (branch_targets[branch_idx].getValue() == branch_tgt.getValue()))
		{
			// BTB hit
			// just update the predictor
			branch_predictors[branch_idx].updateState(taken);
		}
		else
		{
			// BTB miss
			// caused by: (1) access of unused entry, (2) access an entry of another branch, or (3) access of the entry of the very same branch but with another target
			// create new BTB entry (eventually overwrite the old one)
			branch_entry_valid[branch_idx] = true;
			branch_addresses[branch_idx].setValue(branch_pc);
			branch_targets[branch_idx].setValue(branch_tgt);
			if(reset_predictor_on_overwrite)
			{
				branch_predictors[branch_idx].reset();
			}
			// update the predictor
			branch_predictors[branch_idx].updateState(taken);
		}
	}
	
	/**
	 * Looks up the prediction of the branch target buffer for a specific branch.
	 * Notice that depending on when the method is called the branch prediction result my differ. For a branch prediction the lookupBranch() function has to be called at the decode stage before the branch target is calculated. 
	 * @param branch_pc The pc of the branch that is to be looked up
	 * @return Returns the result of the lookup: On branch target buffer hit, either HIT_PREDICT_TAKEN or HIT_PREDICT_NOT_TAKEN is returned (depending on the prediction). On branch target buffer miss, MISS is returned.
	 */
	public BranchTargetBufferLookupResult lookupBranch(uint32 branch_pc)
	{
		int branch_idx = getIndexForBranchPc(branch_pc);
		
		if((branch_entry_valid[branch_idx]) && (branch_addresses[branch_idx].getValue() == branch_pc.getValue()))
		{
			// BTB hit
			if(branch_predictors[branch_idx].predictsTaken())
			{
				// predictor says taken
				return BranchTargetBufferLookupResult.HIT_PREDICT_TAKEN;
			}
			else
			{
				// predictor says not taken
				return BranchTargetBufferLookupResult.HIT_PREDICT_NOT_TAKEN;
			}
		}
		else
		{
			// BTB miss
			if(branch_entry_valid[branch_idx])
			{
				// capacity miss
			}
			else
			{
				// cold / compulsory miss
			}
			return BranchTargetBufferLookupResult.MISS;
		}
	}
	
	/**
	 * Checks if a prediction was performed correctly. The check has to be performed _before_ the branch target buffer is updated for the specific branch (by updateOnBranch())
	 * - Notice: that a jump that is predicted taken, but to another jump target, HAS to be considered as mispredicted.
	 * - Notice: on a branch target buffer miss the prediction is correct, if the predictor predicts that the jump is not taken and it is not taken.
	 * @throws BranchPredictionException 
	 * @returns True if the prediction direction and if jumping the branch target is similar for the given branch, else false.
	 */ 
	public boolean checkPrediction(uint32 branch_pc, uint32 branch_tgt, boolean jumped) throws BranchPredictionException
	{
		boolean result = false;
		switch(lookupBranch(branch_pc))
		{
		case HIT_PREDICT_TAKEN:
			result = ((jumped == true) && (branch_tgt.getValue() == getStoredBranchTarget(branch_pc).getValue()));
			break;
		case HIT_PREDICT_NOT_TAKEN:
			result = (jumped == false);
			break;
		case MISS:
			// The interpretation of non-mapped jumps as correct, iff the jump is not taken and the predictor also predicted this. 
			// Anyhow, this is not correct because actually no misprediction has occurred.
			// result = ((jumped == false)&&(jumped == getPredictorsBranchDecision(branch_pc)));
			
			// Since a btb miss results in a predict not taken, a jump that is actually not taken it is interpreted as correct prediction on btb miss.
			result = (jumped == false); 
			break;
		default:
			throw new BranchPredictionException("Unknown branch prediction result.");
		}
		return result;
	}
	
	/**
	 * Returns the branch target of a branch stored in the branch target buffer
	 * @param branch_pc The program counter of the branch 
	 * @return The branch target of a branch stored in the branch target buffer
	 */
	private uint32 getStoredBranchTarget(uint32 branch_pc)
	{
		return branch_targets[getIndexForBranchPc(branch_pc)];
	}
	
	/**
	 * Returns the predictor state of the selected predictor for a branch in the branch target buffer
	 * @param branch_pc The program counter of the branch 
	 * @return the predictor state of the selected predictor for a branch in the branch target buffer
	 */
	public BranchPredictorState getPredictorState(uint32 branch_pc)
	{
		return branch_predictors[getIndexForBranchPc(branch_pc)].getState();
	}

	/**
	 * Returns the index where a branch is stored in the branch target buffer
	 * Implements the direct-mapped organization of the branch target buffer
	 * @return the index where a branch is stored in the branch target buffer
	 */
	public int getIndexForBranchPc(uint32 branch_pc)
	{
		return branch_pc.getValue() & (btb_size - 1);
	}
	
	/**
	 * Returns the branch target address of a branch target buffer entry selected by a program counter
	 * @param branch_pc The program counter of a branch to select the entry of which the stored branch target is to be returned 
	 * @return The target of the branch entry selected by a program counter stored in the branch target buffer
	 */
	public uint32 getBranchTarget(uint32 branch_pc)
	{
		return new uint32(branch_targets[getIndexForBranchPc(branch_pc)]);
	}
	
}
