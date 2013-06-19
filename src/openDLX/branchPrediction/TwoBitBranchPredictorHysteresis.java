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

import openDLX.datatypes.BranchPredictorState;
import openDLX.exception.BranchPredictionException;

public class TwoBitBranchPredictorHysteresis extends TwoBitBranchPredictor
{
	public TwoBitBranchPredictorHysteresis(BranchPredictorState initialState) throws BranchPredictionException
	{
		super(initialState);
	}

	public void updateState(boolean jumpTaken) throws BranchPredictionException
	{
		switch(current_state)
		{
		case PREDICT_STRONGLY_NOT_TAKEN:
			if(jumpTaken)
			{
				current_state=BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN;
			}
			else
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN;
			}
			break;
		case PREDICT_WEAKLY_NOT_TAKEN:
			if(jumpTaken)
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_TAKEN;
			}
			else
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN;
			}
			break;
		case PREDICT_WEAKLY_TAKEN:
			if(jumpTaken)
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_TAKEN;
			}
			else
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN;
			}
			break;
		case PREDICT_STRONGLY_TAKEN:
			if(jumpTaken)
			{
				current_state=BranchPredictorState.PREDICT_STRONGLY_TAKEN;
			}
			else
			{
				current_state=BranchPredictorState.PREDICT_WEAKLY_TAKEN;
			}
			break;
		default:
			throw new BranchPredictionException("Invalid state of branch predictor: " + current_state);
		}
	}

}
