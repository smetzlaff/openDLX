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
package openDLX.datatypes;

/**
 * Enumeration for all possible internal states of the different branch predictors.
 */
public enum BranchPredictorState
{
	PREDICT_NOT_TAKEN,
	PREDICT_TAKEN,
	PREDICT_WEAKLY_NOT_TAKEN,
	PREDICT_STRONGLY_NOT_TAKEN,
	PREDICT_WEAKLY_TAKEN,
	PREDICT_STRONGLY_TAKEN,
	UNKNOWN;
	
	public String toGuiString()
	{
		switch(this)
		{
		case PREDICT_NOT_TAKEN:
			return "Predict Not Taken (0)";
		case PREDICT_TAKEN:
			return "Predict Taken (1)";
		case PREDICT_WEAKLY_NOT_TAKEN:
			return "Predict Weakly Not Taken (01)";
		case PREDICT_WEAKLY_TAKEN:
			return "Predict Weakly Taken (10)";
		case PREDICT_STRONGLY_NOT_TAKEN:
			return "Predict Strongly Not Taken (00)";
		case PREDICT_STRONGLY_TAKEN:
			return "Predict Strongly Taken (11)";
		case UNKNOWN:
			return "No Initial State";
		default:
			return "Unknown Initial State";
		}
	}
}
