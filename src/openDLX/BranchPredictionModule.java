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
package openDLX;

import java.util.Properties;
import java.util.Queue;

import org.apache.log4j.Logger;

import openDLX.branchPrediction.BranchTargetBuffer;
import openDLX.datatypes.ArchCfg;
import openDLX.datatypes.BranchPredictionModuleExecuteData;
import openDLX.datatypes.BranchPredictionModuleFetchData;
import openDLX.datatypes.BranchPredictionModuleOutputData;
import openDLX.datatypes.BranchPredictorState;
import openDLX.datatypes.BranchPredictorType;
import openDLX.datatypes.BranchTargetBufferLookupResult;
import openDLX.datatypes.ExecuteBranchPredictionData;
import openDLX.datatypes.FetchDecodeData;
import openDLX.datatypes.Instruction;
import openDLX.datatypes.uint32;
import openDLX.exception.BranchPredictionException;
import openDLX.exception.PipelineException;
import openDLX.util.Statistics;

/**
 * @brief Module to encapsulate the branch predictor in the pipeline
 */
public class BranchPredictionModule
{
	/// Logging facility
	private static Logger logger = Logger.getLogger("BP_MODULE");
	/// The actual branch target buffer with branch predictors 
	private BranchTargetBuffer btb;
	/// Central module for simulation statistics 
	private Statistics stat;
	/// Input latch for the branch predictor module (table update part)
	private Queue<ExecuteBranchPredictionData> execute_branchprediction_latch;
	/// Input latch for the branch predictor module (table lookup part)
	private Queue<FetchDecodeData> fetch_branchprediction_latch;

	/**
	 * @brief Constructor
	 * @param config Configuration object, containing the branch predictor configuration.
	 * Currently the configuration entries:\n
	 * - btb_size - determine the size of the branch target buffer
	 * - btb_predictor - set the used predictor type (BranchPredictorType)
	 * - btb_predictor_initial_state - defines the initial state of the predictors (BranchPredictorState)
	 * - btb_predictor_reset_on_overwrite - defines the behavior on overwriting a branch target buffer entry by another branch (if true, which is not recommended, the predictor is reset to the initial state on branch target buffer miss)\n
	 * are supported.
	 * \sa BranchPredictorType, BranchPredictorState
	 * @throws PipelineException 
	 */
	public BranchPredictionModule(Properties config) throws PipelineException
	{
		// obtain settings for the BTB
		int btb_size = 1;
		if(config.getProperty("btb_size")!=null)
		{
			btb_size = Integer.decode(config.getProperty("btb_size"));
		}
		
		// get the predictor type, default value is S_ALWAYS_NOT_TAKEN
		BranchPredictorType btb_predictor = BranchPredictorType.S_ALWAYS_NOT_TAKEN;
		if(config.getProperty("btb_predictor")!=null)
		{
			btb_predictor = getBranchPredictorTypeFromString(config.getProperty("btb_predictor"));
		}
		// also set the architecture variable (just for completeness) 
		ArchCfg.branch_predictor_type = btb_predictor;
		
		// get the predictor initial state, default value is PREDICT_NOT_TAKEN
		// Notice each predictor may have a different set of supported predictor states. 
		BranchPredictorState btb_predictor_initial_state = BranchPredictorState.PREDICT_NOT_TAKEN;
		if(config.getProperty("btb_predictor_initial_state")!=null)
		{
			btb_predictor_initial_state = getBranchPredictorInitialStateFromString(config.getProperty("btb_predictor_initial_state"));
		}
		
		// get the behavior if a btb entry is overwritten, the recommended default is false 
		boolean btb_predictor_reset_on_overwrite = false;
		if(config.getProperty("btb_predictor_reset_on_overwrite")!=null)
		{
			if(Integer.decode(config.getProperty("btb_predictor_reset_on_overwrite"))==0)
			{
				btb_predictor_reset_on_overwrite = false;
			}
			else
			{
				btb_predictor_reset_on_overwrite = true;
			}
		}
		
		btb = new BranchTargetBuffer(btb_size, btb_predictor, btb_predictor_initial_state, btb_predictor_reset_on_overwrite);
		
		// get statistics object and set btb config
		stat = Statistics.getInstance();
		stat.setBTBConfig(btb_size, btb_predictor);
		
	}

	/**
	 * Parses the branch predictor selection string of the configuration file and returns the result as BranchPredictorType
	 * @param bp_type The string selecting the used branch predictor
	 * @return The selected branch predictor as BranchPredictorType
	 */
	public static BranchPredictorType getBranchPredictorTypeFromString(String bp_type)
	{
		if(bp_type.compareTo(BranchPredictorType.S_ALWAYS_NOT_TAKEN.toString()) == 0)
		{
			return BranchPredictorType.S_ALWAYS_NOT_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.S_ALWAYS_TAKEN.toString()) == 0)
		{
			return BranchPredictorType.S_ALWAYS_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.S_BACKWARD_TAKEN.toString()) == 0)
		{
			return BranchPredictorType.S_BACKWARD_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_1BIT.toString()) == 0)
		{
			return BranchPredictorType.D_1BIT;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_2BIT_SATURATION.toString()) == 0)
		{
			return BranchPredictorType.D_2BIT_SATURATION;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_2BIT_HYSTERESIS.toString()) == 0)
		{
			return BranchPredictorType.D_2BIT_HYSTERESIS;
		}
		
		return BranchPredictorType.UNKNOWN;
	}
	
	
	/**
	 * Parses the branch predictor selection string of the gui options window and returns the result as BranchPredictorType
	 * @param bp_type The string selecting the used branch predictor
	 * @return The selected branch predictor as BranchPredictorType
	 */
	public static BranchPredictorType getBranchPredictorTypeFromGuiString(String bp_type)
	{
		if(bp_type.compareTo(BranchPredictorType.S_ALWAYS_NOT_TAKEN.toGuiString()) == 0)
		{
			return BranchPredictorType.S_ALWAYS_NOT_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.S_ALWAYS_TAKEN.toGuiString()) == 0)
		{
			return BranchPredictorType.S_ALWAYS_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.S_BACKWARD_TAKEN.toGuiString()) == 0)
		{
			return BranchPredictorType.S_BACKWARD_TAKEN;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_1BIT.toGuiString()) == 0)
		{
			return BranchPredictorType.D_1BIT;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_2BIT_SATURATION.toGuiString()) == 0)
		{
			return BranchPredictorType.D_2BIT_SATURATION;
		}
		else if(bp_type.compareTo(BranchPredictorType.D_2BIT_HYSTERESIS.toGuiString()) == 0)
		{
			return BranchPredictorType.D_2BIT_HYSTERESIS;
		}
		
		return BranchPredictorType.UNKNOWN;
	}
	
	/**
	 * Parses the branch predictor initial state setting string of the configuration file and returns the result as BranchPredictorState
	 * @param bp_initial_state The string selecting the initial branch predictor state
	 * @return The initial branch predictor state as BranchPredictorState
	 */
	public static BranchPredictorState getBranchPredictorInitialStateFromString(String bp_initial_state)
	{
		if(bp_initial_state.compareTo(BranchPredictorState.PREDICT_NOT_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_WEAKLY_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_WEAKLY_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_STRONGLY_TAKEN.toString())==0)
		{
			return BranchPredictorState.PREDICT_STRONGLY_TAKEN;
		}
		
		return BranchPredictorState.UNKNOWN;
	}
	
	/**
	 * Parses the branch predictor initial state setting string of gui options window and returns the result as BranchPredictorState
	 * @param bp_initial_state The string selecting the initial branch predictor state
	 * @return The initial branch predictor state as BranchPredictorState
	 */
	public static BranchPredictorState getBranchPredictorInitialStateFromGuiString(String bp_initial_state)
	{
		if(bp_initial_state.compareTo(BranchPredictorState.PREDICT_NOT_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_WEAKLY_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_WEAKLY_TAKEN;
		}
		else if (bp_initial_state.compareTo(BranchPredictorState.PREDICT_STRONGLY_TAKEN.toGuiString())==0)
		{
			return BranchPredictorState.PREDICT_STRONGLY_TAKEN;
		}
		
		return BranchPredictorState.UNKNOWN;
	}
	
	/**
	 * Sets the input latch for the synchronous operation of the branch prediction module
	 * @param executeBranchpredictionLatch The input latch containing all necessary information for the update part of the branch prediction (altering the predictors) 
	 * @param fetchBranchPredictionLatch The input latch containing the necessary information for the lookup part of the branch prediction (predicting jumps)
	 */
	public void setInputLatches(Queue<ExecuteBranchPredictionData> executeBranchpredictionLatch, Queue<FetchDecodeData> fetchBranchPredictionLatch)
	{
		execute_branchprediction_latch = executeBranchpredictionLatch;
		fetch_branchprediction_latch = fetchBranchPredictionLatch;
	}

	public BranchPredictionModuleOutputData doCycle() throws BranchPredictionException
	{

		// lookup for jump target
		BranchPredictionModuleOutputData bpmod = lookupTables();
		
		// update prediction tables
		updateTables();
		
		return bpmod;
	}
	
	/**
	 * The synchronous operation of the branch prediction module
	 * @throws BranchPredictionException 
	 */
	public void updateTables() throws BranchPredictionException
	{
		ExecuteBranchPredictionData ebd = execute_branchprediction_latch.element();
		Instruction inst = ebd.getInst();
		uint32 branch_pc = ebd.getBranchPc();
		uint32 branch_tgt = ebd.getBranchTgt();
		boolean jump = ebd.getJumpTaken();
		
		if(inst.getBranch())
		{
			logger.info("Jump from " + branch_pc.getValueAsHexString() + " to " + branch_tgt.getValueAsHexString() + " that is |" + ((jump)?("taken"):("not taken")) + "| was predicted: |" + ((btb.checkPrediction(branch_pc, branch_tgt, jump)?("correctly"):("not correctly"))) + "| BTB said: |" + btb.lookupBranch(branch_pc) + "| BTB entry: |" + btb.getIndexForBranchPc(branch_pc) + "| predictor state: |" + btb.getPredictorState(branch_pc) + "|");
			stat.countBranchInformation(branch_pc, btb.getIndexForBranchPc(branch_pc), branch_tgt, jump, btb.lookupBranch(branch_pc), btb.checkPrediction(branch_pc, branch_tgt, jump));
			stat.countPredictions(btb.checkPrediction(branch_pc, branch_tgt, jump));
			stat.countBTBAccesses(btb.lookupBranch(branch_pc));
			btb.updateOnBranch(branch_pc, branch_tgt, jump);
		}
	}
	
	public BranchPredictionModuleOutputData lookupTables()
	{
		FetchDecodeData fdd = fetch_branchprediction_latch.element();
		
		uint32 pc = fdd.getPc();
		boolean do_speculative_jump = false;
		uint32 branch_tgt = new uint32(0);
		
		BranchTargetBufferLookupResult result = btb.lookupBranch(pc);
		
		if(result == BranchTargetBufferLookupResult.HIT_PREDICT_TAKEN)
		{
			do_speculative_jump = true;
			branch_tgt = btb.getBranchTarget(pc);
		}
		
		if((result == BranchTargetBufferLookupResult.HIT_PREDICT_NOT_TAKEN) || (result == BranchTargetBufferLookupResult.HIT_PREDICT_TAKEN))
		{
			logger.debug("instruction at: " + pc.getValueAsHexString() + " found in BTB and is predicted as " + ((do_speculative_jump)?("taken to addr: " + branch_tgt.getValueAsHexString()):("not taken")));
		}
		else if(result == BranchTargetBufferLookupResult.MISS)
		{
			logger.debug("instruction at: " + pc.getValueAsHexString() + " was not found in BTB");
		}
		
		BranchPredictionModuleFetchData bpmfd = new BranchPredictionModuleFetchData(do_speculative_jump, pc, branch_tgt);
		BranchPredictionModuleExecuteData bpmed = new BranchPredictionModuleExecuteData(do_speculative_jump, pc, branch_tgt);
		return new BranchPredictionModuleOutputData(bpmfd, bpmed);
	}

}
