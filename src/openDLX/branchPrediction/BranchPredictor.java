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

/**
 * Interface that all branch predictors have to implement.
 */
public interface BranchPredictor
{
	/**
	 * Returns true if the predictor predicts a branch as taken, else false
	 * @return True if the predictor predicts a branch as taken, else false
	 */
	public boolean predictsTaken();
	
	/**
	 * Returns true if the predictor predicts a branch as not taken, else false
	 * @return True if the predictor predicts a branch as not taken, else false
	 */
	public boolean predictsNotTaken();
	
	/**
	 * Returns the internal state of the predictor
	 * @return the internal state of the predictor
	 */
	public BranchPredictorState getState();
	
	/**
	 * Updates the internal state of the predictor if a jump was taken or not taken
	 * @param jumpTaken True if the jump was taken, else false
	 * @throws BranchPredictionException 
	 */
	public void updateState(boolean jumpTaken) throws BranchPredictionException;
	
	/**
	 * Resets the branch predictor to its initial state
	 */
	public void reset();
}
