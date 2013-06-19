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

import java.util.Queue;
import openDLX.datatypes.*;
import openDLX.memory.DataMemory;
import openDLX.memory.InstructionMemory;
import openDLX.memory.MainMemory;

public class PipelineContainer {
	private MainMemory mem;
	private InstructionMemory imem;
	private DataMemory dmem;
	private Fetch fetch;
	private RegisterSet reg_set;
	private Decode decode;
	private Execute execute;
	private BranchPredictionModule branch_prediction_module;
	private Memory memory;
	private WriteBack writeback;
	private Queue<FetchDecodeData> fetch_decode_latch;
	private Queue<DecodeExecuteData> decode_execute_latch;
	private Queue<BranchPredictionModuleFetchData> branchprediction_fetch_latch;
	private Queue<BranchPredictionModuleExecuteData> branchprediction_execute_latch;
	private Queue<ExecuteMemoryData> execute_memory_latch;
	private Queue<ExecuteFetchData> execute_fetch_latch;
	private Queue<ExecuteBranchPredictionData> execute_branchprediction_latch;
	private Queue<MemoryWritebackData> memory_writeback_latch;
	private Queue<WriteBackData> writeback_latch;
	
	public MainMemory getMainMemory() {
		return mem;
	}
	public void setMainMemory(MainMemory mem) {
		this.mem = mem;
	}
	public InstructionMemory getInstructionMemory() {
		return imem;
	}
	public void setInstructionMemory(InstructionMemory imem) {
		this.imem = imem;
	}
	public DataMemory getDataMemory() {
		return dmem;
	}
	public void setDataMemory(DataMemory dmem) {
		this.dmem = dmem;
	}
	public Fetch getFetchStage() {
		return fetch;
	}
	public void setFetchStage(Fetch fetch) {
		this.fetch = fetch;
	}
	public RegisterSet getRegisterSet() {
		return reg_set;
	}
	public void setRegisterSet(RegisterSet reg_set) {
		this.reg_set = reg_set;
	}
	public Decode getDecodeStage() {
		return decode;
	}
	public void setDecodeStage(Decode decode) {
		this.decode = decode;
	}
	public Execute getExecuteStage() {
		return execute;
	}
	public void setExecuteStage(Execute execute) {
		this.execute = execute;
	}
	public BranchPredictionModule getBranchPredictionModule() {
		return branch_prediction_module;
	}
	public void setBranchPredictionModule(BranchPredictionModule branch_prediction_module) {
		this.branch_prediction_module = branch_prediction_module;
	}
	public Memory getMemoryStage() {
		return memory;
	}
	public void setMemoryStage(Memory memory) {
		this.memory = memory;
	}
	public WriteBack getWriteBackStage() {
		return writeback;
	}
	public void setWriteBackStage(WriteBack writeback) {
		this.writeback = writeback;
	}
	public Queue<FetchDecodeData> getFetchDecodeLatch() {
		return fetch_decode_latch;
	}
	public void setFetchDecodeLatch(Queue<FetchDecodeData> fetch_decode_latch) {
		this.fetch_decode_latch = fetch_decode_latch;
	}
	public Queue<DecodeExecuteData> getDecodeExecuteLatch() {
		return decode_execute_latch;
	}
	public void setDecodeExecuteLatch(Queue<DecodeExecuteData> decode_execute_latch) {
		this.decode_execute_latch = decode_execute_latch;
	}
	public Queue<BranchPredictionModuleFetchData> getBranchPredictionFetchLatch() {
		return branchprediction_fetch_latch;
	}
	public void setBranchPredictionFetchLatch(
			Queue<BranchPredictionModuleFetchData> branchprediction_fetch_latch) {
		this.branchprediction_fetch_latch = branchprediction_fetch_latch;
	}
	public Queue<BranchPredictionModuleExecuteData> getBranchPredictionExecuteLatch() {
		return branchprediction_execute_latch;
	}
	public void setBranchPredictionExecuteLatch(
			Queue<BranchPredictionModuleExecuteData> branchprediction_execute_latch) {
		this.branchprediction_execute_latch = branchprediction_execute_latch;
	}
	public Queue<ExecuteMemoryData> getExecuteMemoryLatch() {
		return execute_memory_latch;
	}
	public void setExecuteMemoryLatch(Queue<ExecuteMemoryData> execute_memory_latch) {
		this.execute_memory_latch = execute_memory_latch;
	}
	public Queue<ExecuteFetchData> getExecuteFetchLatch() {
		return execute_fetch_latch;
	}
	public void setExecuteFetchLatch(Queue<ExecuteFetchData> execute_fetch_latch) {
		this.execute_fetch_latch = execute_fetch_latch;
	}
	public Queue<ExecuteBranchPredictionData> getExecuteBranchPredictionLatch() {
		return execute_branchprediction_latch;
	}
	public void setExecuteBranchPredictionLatch(
			Queue<ExecuteBranchPredictionData> execute_branchprediction_latch) {
		this.execute_branchprediction_latch = execute_branchprediction_latch;
	}
	public Queue<MemoryWritebackData> getMemoryWriteBackLatch() {
		return memory_writeback_latch;
	}
	public void setMemoryWriteBackLatch(Queue<MemoryWritebackData> memory_writeback_latch) {
		this.memory_writeback_latch = memory_writeback_latch;
	}
	public Queue<WriteBackData> getWriteBackLatch() {
		return writeback_latch;
	}
	public void setWriteBackLatch(Queue<WriteBackData> writeback_latch) {
		this.writeback_latch = writeback_latch;
	}
}
