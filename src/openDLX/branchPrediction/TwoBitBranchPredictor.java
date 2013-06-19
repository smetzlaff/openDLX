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

public abstract class TwoBitBranchPredictor implements BranchPredictor
{
	protected BranchPredictorState initial_state;
	protected BranchPredictorState current_state;
	
	public TwoBitBranchPredictor(BranchPredictorState initialState) throws BranchPredictionException
	{
		// notice the 1B predictor has only the two states taken and not taken.
		if((initialState != BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN) && (initialState != BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN) && (initialState != BranchPredictorState.PREDICT_STRONGLY_TAKEN) && (initialState != BranchPredictorState.PREDICT_WEAKLY_TAKEN))
		{
			throw new BranchPredictionException("Invalid initial state of branch predictor: " + initialState);
		}
		
		initial_state = initialState;
		current_state = initialState;
	}

	public BranchPredictorState getState()
	{
		return current_state;
	}

	public boolean predictsTaken()
	{
		if((current_state == BranchPredictorState.PREDICT_STRONGLY_TAKEN) || (current_state == BranchPredictorState.PREDICT_WEAKLY_TAKEN))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean predictsNotTaken()
	{
		return !predictsTaken();
	}

	public void reset()
	{
		current_state = initial_state;
	}
	
	public String toString()
	{
		return new String(this.getClass().getSimpleName() + " c: " + current_state + " i: " + initial_state);
	}
}
