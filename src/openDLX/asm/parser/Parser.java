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
package openDLX.asm.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import openDLX.asm.AssemblerException;
import openDLX.asm.MemoryBuffer;
import openDLX.asm.instruction.Instruction;
import openDLX.asm.instruction.InstructionException;
import openDLX.asm.instruction.Registers;
import openDLX.asm.tokenizer.Token;
import openDLX.asm.tokenizer.TokenType;
import openDLX.asm.tokenizer.Tokenizer;
import openDLX.asm.tokenizer.TokenizerException;

public class Parser {
	private static final String INCOMPLETE_DIRECTIVE = "incomplete directive";
	private static final String INCOMPLETE_INSTRUCTION = "incomplete instruction";
	private static final String INSTRUCTION_EXCEPTION = "instruction: ";
	private static final String LABEL_ALREADY_EXISTS = "label already exists";
	private static final String LABEL_DOES_NOT_EXISTS = "label does not exist";
	private static final String LABEL_NOT_ALLOWED_HERE = "Label not allowed here";
	private static final String MISSING_PARANTHESIS = "missing paranthesis";
	private static final String MISSING_SEPARATOR = "missing separator before";
	private static final String NO_NOP = "this is no nop instruction";
	private static final String NOT_A_NUMBER = "expected number or label but got";
	private static final String NOT_A_REGISTER = "expected register specifier but got";
	private static final String NUMBER_NEGATIVE = "negative value not allowed here";
	private static final String NUMBER_TOO_BIG = "number is too big or too small";
	private static final String TEXT_OVERFLOW = "text segment overflow";
	private static final String UNKNOWN_MNEMONIC = "unknown mnemonic";
	private static final String UNKNOWN_MNEMONIC_TYPE = "unknown mnemonic type";
	private static final String UNEXPECTED_LITERAL_END = "unexpected end of string literal";
	private static final String UNEXPECTED_TOKEN = "unexpected token";
	private static final String UNEXPECTED_TRASH = "unexpected trash at end of line";
	private static final String UNKNOWN_DIRECTIVE = "unknown directive";
	private static final String UNKNOWN_TRAP_ID = "unknown trap id";

	/*
	 * This parser runs up to two times over the code.
	 * The first run tries to resolve all labels.
	 * Instructions with labels that are unknown on the first run
	 * are saved in unresolvedInstructions for the second run. 
	 */
	private MemoryBuffer memory_; //where binary output is saved
	//however, there is no linker and hence no distinction between local and global labels
	private Hashtable<String, Integer> globalLabels_;
	private Tokenizer tokenizer_;
	private SegmentPointer dataPointer_; //data segment pointer
	private SegmentPointer textPointer_; //text segment pointer
	private SegmentPointer segmentPointer_; //active segment pointer
	private boolean stopOnUnresolvedLabel; //turned to true on second run
	private List<UnresolvedInstruction> unresolvedInstructions_; //
	private boolean hasGlobalMain; //workaround unnecessary when distinction between global and local labels  

	public boolean hasGlobalMain() {
		return hasGlobalMain;
	}

	public Parser(int dataSegment, int textSegment) {
		tokenizer_ = new Tokenizer();
		dataPointer_ = new SegmentPointer(dataSegment);
		textPointer_ = new SegmentPointer(textSegment);
		segmentPointer_ = textPointer_;
	}

	/*
	 * ================================* Parse *================================
	 */
	/**
	 * another parse pass for jet unresolved labels, stop on any unresolved
	 * label
	 * 
	 * @param unresolvedInstructions
	 * @param globalLabels
	 * @param memory
	 * @throws ParserException
	 */
	public void resolve(List<UnresolvedInstruction> unresolvedInstructions,
			Hashtable<String, Integer> globalLabels, MemoryBuffer memory) throws AssemblerException {
		stopOnUnresolvedLabel = true;
		unresolvedInstructions_ = unresolvedInstructions;
		globalLabels_ = globalLabels;
		memory_ = memory;
		for (UnresolvedInstruction instr : unresolvedInstructions_) {
			if (instr.inTextSegement)
				segmentPointer_ = textPointer_;
			else
				segmentPointer_ = dataPointer_;
			segmentPointer_.set(instr.position);
			parseLine(instr.tokens);
		}
	}

	/**
	 * parse stream into memory, unresolved instructions are returned
	 * 
	 * @param reader
	 * @param globalLabels
	 * @param memory
	 * @return
	 * @throws TokenizerException
	 * @throws IOException
	 * @throws ParserException
	 */
	public List<UnresolvedInstruction> parse(BufferedReader reader,
			Hashtable<String, Integer> globalLabels, MemoryBuffer memory) throws IOException,
			AssemblerException {
		stopOnUnresolvedLabel = false;
		globalLabels_ = globalLabels;
		tokenizer_.setReader(reader);
		memory_ = memory;
		unresolvedInstructions_ = new ArrayList<UnresolvedInstruction>();

		Token[] tokens = tokenizer_.readLine();
		while (tokens != null) {
			parseLine(tokens);
			tokens = tokenizer_.readLine();
		}
		return unresolvedInstructions_;
	}

	/**
	 * called from parse(...) and resolve(...)
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void parseLine(Token[] tokens) throws AssemblerException {
		if (tokens.length == 0)
			return;
		Token[] tmpTokens;
		try {
			switch (tokens[0].getTokenType()) {
			case Label:
				String label = tokens[0].getString();
				addLabel(label.substring(0, label.length() - 1));
				tmpTokens = new Token[tokens.length - 1];
				System.arraycopy(tokens, 1, tmpTokens, 0, tokens.length - 1);
				parseLine(tmpTokens);
				break;
			case Mnemonic:
				parseMnemonic(tokens);
				break;
			case Directive:
				parseDirectives(tokens);
				break;
			default:
				throw new ParserException(UNEXPECTED_TOKEN, tokens[0]);
			}
		} catch (ParserException ex) {
			if (ex.getToken() == null)
				ex.setToken(tokens[0]);
			throw ex;
		}
	}

	/*
	 * ===============================* Labels *===============================
	 */
	/**
	 * add label to labels if not exists
	 * 
	 * @param label
	 * @throws ParserException
	 */
	private void addLabel(String label) throws ParserException {
		if (globalLabels_.containsKey(label))
			throw new ParserException(LABEL_ALREADY_EXISTS, null);
		globalLabels_.put(label, segmentPointer_.get());
	}

	/**
	 * replaces labels with their corresponding integer constant. If not
	 * possible returns false
	 * 
	 * @param tokens
	 * @return
	 */
	private Token resolveLabels(Token[] tokens) {
		for (int i = 0; i < tokens.length; ++i) {
			Token t = tokens[i];
			if (t.getTokenType() == TokenType.Identifier) {
				Integer position = globalLabels_.get(t.getString());
				if (position == null)
					return t;
				tokens[i] = new Token(t.getPosition(), TokenType.IntegerConstant,
						position.toString());
			}
		}
		return null;
	}

	/*
	 * ==============================* Mnemonics *==============================
	 */
	/**
	 * ALU_IMMEDIATE ALU_REGISTER BRANCH JUMP JUMP_REGISTER LOAD LOAD_IMMEDIATE
	 * NOP SAVE SHIFT_IMMEDIATE TRAP
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void parseMnemonic(Token[] tokens) throws ParserException {
		Instruction instr = Instruction.fromMnemonic(tokens[0].getString());

		//unknown mnemonic
		if (instr == null)
			throw new ParserException(UNKNOWN_MNEMONIC, tokens[0]);

		//unresolved labels
		Token t = resolveLabels(tokens);
		if (t != null) {
			if (stopOnUnresolvedLabel == true)
				throw new ParserException(LABEL_DOES_NOT_EXISTS, t);
			unresolvedInstructions_.add(new UnresolvedInstruction(tokens, segmentPointer_.get(),
					segmentPointer_ == textPointer_ ? true : false));
		} else {
			switch (instr.calcInstType()) {
			case ALU_IMMEDIATE:
				aluImmediate(instr, tokens);
				break;
			case ALU_REGISTER:
				aluRegister(instr, tokens);
				break;
			case BRANCH:
				branch(instr, tokens);
				break;
			case JUMP:
				jump(instr, tokens);
				break;
			case JUMP_REGISTER:
				jumpRegister(instr, tokens);
				break;
			case LOAD:
				load(instr, tokens);
				break;
			case LOAD_IMMEDIATE:
				loadImmediate(instr, tokens);
				break;
			case NOP:
				nop(instr, tokens);
				break;
			case SAVE:
				save(instr, tokens);
				break;
			case SHIFT_REGISTER:
				shiftRegister(instr, tokens);
				break;
			case SHIFT_IMMEDIATE:
				shiftImmediate(instr, tokens);
				break;
			case TRAP:
				trap(instr, tokens);
				break;
			default:
				throw new ParserException(UNKNOWN_MNEMONIC_TYPE, tokens[0]);
			}
			if (memory_.getTextEnd() >= memory_.getDataBegin()
					&& memory_.getTextEnd() <= memory_.getDataEnd()) {
				//TODO translate text data? throw exception?
				throw new ParserException(TEXT_OVERFLOW, tokens[0]);
			}
			memory_.writeWord(segmentPointer_.get(), instr.instrWord());
		}
		segmentPointer_.add(4);
		memory_.setTextEnd(segmentPointer_.get());
	}

	/**
	 * e.g. addi r1,r0,200
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void aluImmediate(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r0
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRs(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//+-200
			++i;
			boolean negative = false;
			if (tokens[i].getString().equals("+")) {
				negative = false;
				++i;
			} else if (tokens[i].getString().equals("-")) {
				negative = true;
				++i;
			}
			value = Integer.decode(tokens[i].getString());
			if (negative)
				value = -value;
			instr.setOffset(value);
			if (i < tokens.length - 1) {
				throw new ParserException(UNEXPECTED_TRASH, tokens[++i]);
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. add r1,r2,r3
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void aluRegister(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRd(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r2
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRs(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r3
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		}
	}
	
	/**
	 * e.g. sll(v) r1,r2,r3
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void shiftRegister(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRd(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r2
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r3
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRs(value);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		}
	}

	/**
	 * e.g. beqz r1,Label+4
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void branch(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRs(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//Label+4
			++i;
			value = Integer.decode(tokens[i].getString());
			value -= segmentPointer_.get() + 4;
			instr.setOffset(value >> 2);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. j Label+4
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void jump(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			//Label+4
			++i;
			Integer value = Integer.decode(tokens[i].getString());
			//i -= textSegment_ + 4;
			instr.setInstrIndex(value >> 2);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. jr r31
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void jumpRegister(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			//r31
			++i;
			Integer value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRs(value);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		}
	}

	/**
	 * e.g. lb r1,Label+4(r2)
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void load(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
			//,	
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//optional Label
			if (!tokens[i + 1].getString().equals("(")) {
				++i;
				value = Integer.decode(tokens[i].getString());
				instr.setOffset(value);
			}
			//TODO do it better
			//optional +4
			++i;
			if (tokens.length > i && tokens[i].getString().equals("+")) {
				++i;
				value += Integer.decode(tokens[i].getString());
				instr.setOffset(value);
				++i;
			}
			if (tokens.length > i && tokens[i].getString().equals("-")) {
				++i;
				value -= Integer.decode(tokens[i].getString());
				instr.setOffset(value);
				++i;
			}

			//optional (r2)
			if (tokens.length <= i)
				return;
			//(
			if (!tokens[i].getString().equals("("))
				throw new ParserException(MISSING_PARANTHESIS, tokens[i]);
			//r2
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setBase(value);
			//)
			++i;
			if (!tokens[i].getString().equals(")"))
				throw new ParserException(MISSING_PARANTHESIS, tokens[i]);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. lhi r1,Label+4
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void loadImmediate(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//Label+4
			++i;
			boolean negative = false;
			if (tokens[i].getString().equals("+")) {
				negative = false;
				++i;
			} else if (tokens[i].getString().equals("-")) {
				negative = true;
				++i;
			}
			value = Integer.decode(tokens[i].getString());
			if (negative)
				value = -value;
			instr.setOffset(value);
			if (i < tokens.length - 1) {
				throw new ParserException(UNEXPECTED_TRASH, tokens[++i]);
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * nop
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void nop(Instruction instr, Token[] tokens) throws ParserException {
		//TODO: there must be a better solution for the same opcode problem with slli and nop
		if (tokens[0].getString().equalsIgnoreCase("slli")) {
			shiftImmediate(instr, tokens);
			return;
		}
		if (!tokens[0].getString().equalsIgnoreCase("nop"))
			throw new ParserException(NO_NOP, tokens[0]);
		if (tokens.length != 1)
			throw new ParserException(UNEXPECTED_TRASH, tokens[1]);
	}

	/**
	 * e.g. sb Label+4(r0),r1
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void save(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value = new Integer(0);
			//optional Label
			if (!tokens[i + 1].getString().equals("(")) {
				++i;
				value = Integer.decode(tokens[i].getString());
				instr.setOffset(value);
			}

			//TODO do it better
			//optional +4
			++i;
			if (tokens[i].getString().equals("+")) {
				++i;
				value += Integer.decode(tokens[i].getString());
				instr.setOffset(value);
				++i;
			}
			if (tokens[i].getString().equals("-")) {
				++i;
				value -= Integer.decode(tokens[i].getString());
				instr.setOffset(value);
				++i;
			}

			//optional (r0)
			if (tokens[i].getString().equals("(")) {
				//(
				if (!tokens[i].getString().equals("("))
					throw new ParserException(MISSING_PARANTHESIS, tokens[i]);
				//r0
				++i;
				value = Registers.instance().getInteger(tokens[i].getString());
				instr.setBase(value);
				//)
				++i;
				if (!tokens[i].getString().equals(")"))
					throw new ParserException(MISSING_PARANTHESIS, tokens[i]);
				++i;
			}
			//,
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. slli r2,r1,2
	 * 
	 * @param instr
	 * @param tmpTokens
	 * @return
	 * @throws ParserException
	 */
	private void shiftImmediate(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			Integer value;
			//r2
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRd(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//r1
			++i;
			value = Registers.instance().getInteger(tokens[i].getString());
			instr.setRt(value);
			//,
			++i;
			if (!tokens[i].getString().equals(","))
				throw new ParserException(MISSING_SEPARATOR, tokens[i]);
			//2
			++i;
			value = Integer.decode(tokens[i].getString());
			instr.setSa(value);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NullPointerException ex) {
			throw new ParserException(NOT_A_REGISTER, tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/**
	 * e.g. trap 0
	 * 
	 * @param instr
	 * @param tokens
	 * @return
	 * @throws ParserException
	 */
	private void trap(Instruction instr, Token[] tokens) throws ParserException {
		int i = 0;
		try {
			++i;
			int trapId = Integer.decode(tokens[i].getString());
			if (trapId < 0 || trapId > 5)
				throw new ParserException(UNKNOWN_TRAP_ID, tokens[i]);
			instr.setRs(trapId);
			//if someone wants an explicit breakdown
			/*switch (trapId) {
			//terminate
			case 0:
				break;
			//open file
			case 1:
				break;
			//close file
			case 2:
				break;
			//read file
			case 3:
				break;
			//write file
			case 4:
				break;
			//formatted output to std out
			case 5:
				break;
			default:
				throw new ParserException("", null);
			}*/
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		} catch (InstructionException ex) {
			throw new ParserException(INSTRUCTION_EXCEPTION + ex.getMessage(), tokens[i]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i]);
		}
	}

	/*
	 * =============================* Directives *=============================
	 */
	/**
	 * .align .ascii .asciiz .byte .data .global .half .space .text .word
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void parseDirectives(Token[] tokens) throws ParserException {
		//unresolved labels
		Token t = resolveLabels(tokens);
		if (t != null) {
			if (stopOnUnresolvedLabel == true)
				throw new ParserException(LABEL_DOES_NOT_EXISTS, t);
			unresolvedInstructions_.add(new UnresolvedInstruction(tokens, segmentPointer_.get(),
					segmentPointer_ == textPointer_ ? true : false));
			if (tokens[0].getString().equalsIgnoreCase(".word")) {
				segmentPointer_.add(4);
			} else if (tokens[0].getString().equalsIgnoreCase(".global")) {
				//do nothing
			} else {
				throw new ParserException(LABEL_NOT_ALLOWED_HERE, t);
			}
			return;
		}

		String name = tokens[0].getString();
		if (name.equalsIgnoreCase(".align")) {
			align(tokens);
		} else if (name.equalsIgnoreCase(".ascii")) {
			ascii(tokens);
		} else if (name.equalsIgnoreCase(".asciiz")) {
			ascii(tokens);
			memory_.writeByte(segmentPointer_.get(), (byte) 0x0);//terminating null
			segmentPointer_.add(1);
			memory_.setDataEnd(segmentPointer_.get());
		} else if (name.equalsIgnoreCase(".byte")) {
			byteDir(tokens);
		} else if (name.equalsIgnoreCase(".data")) {
			data(tokens);
		} else if (name.equalsIgnoreCase(".global")) {
			global(tokens);
		} else if (name.equalsIgnoreCase(".half")) {
			half(tokens);
		} else if (name.equalsIgnoreCase(".space")) {
			space(tokens);
		} else if (name.equalsIgnoreCase(".text")) {
			text(tokens);
		} else if (name.equalsIgnoreCase(".word")) {
			word(tokens);
		} else {
			throw new ParserException(UNKNOWN_DIRECTIVE, null);
		}
	}

	/**
	 * e.g. .align 2
	 * 
	 * @param tokens
	 */
	private void align(Token[] tokens) throws ParserException {
		try {
			int align = Integer.decode(tokens[1].getString());
			if (align > 0 && align < 32)
				align = 2 << align - 1;
			else if (align == 0)
				align = 1;
			else
				throw new ParserException(NUMBER_TOO_BIG, tokens[1]);
			while (segmentPointer_.get() % align != 0)
				segmentPointer_.add(1);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[1]);
		}
	}

	/**
	 * e.g. .ascii "Foo\n"
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void ascii(Token[] tokens) throws ParserException {
		try {
			byte[] str = replaceEscapeSequences(tokens[1].getString()).getBytes();
			for (int i = 0; i < str.length; ++i) {
				memory_.writeByte(segmentPointer_.get(), str[i]);
				segmentPointer_.add(1);
				memory_.setDataEnd(segmentPointer_.get());
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch(ParserException ex) {
			throw new ParserException(ex.getMessage(), tokens[1]);
		}
	}

	/**
	 * e.g. .byte 1,2,3
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void byteDir(Token[] tokens) throws ParserException {
		int i = 0;
		try {
			i = 1;
			do {
				int value = Integer.decode(tokens[i++].getString());
				memory_.writeByte(segmentPointer_.get(), (byte) value);
				segmentPointer_.add(1);
				memory_.setDataEnd(segmentPointer_.get());
			} while (i < tokens.length && tokens[i++].getString().equals(","));
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i - 1]);
		}
	}

	/**
	 * e.g. .data 0x1000
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void data(Token[] tokens) throws ParserException {
		segmentPointer_ = dataPointer_;
		if (tokens.length == 1) {
			return;
		} else if (tokens.length == 2) {
			try {
				int value = Integer.decode(tokens[1].getString());
				if (value < 0)
					throw new ParserException(NUMBER_NEGATIVE, tokens[1]);
				dataPointer_.set(value);
				memory_.setDataBegin(value);
			} catch (NumberFormatException ex) {
				throw new ParserException(NOT_A_NUMBER, tokens[1]);
			}
		} else {
			throw new ParserException(UNEXPECTED_TRASH, tokens[2]);
		}
	}

	/**
	 * e.g. .global Label
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void global(Token[] tokens) throws ParserException {
		//TODO or not to do that's the question
		try {
			if (Integer.decode(tokens[1].getString()).intValue() == globalLabels_.get("main")) {
				hasGlobalMain = true;
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_INSTRUCTION, tokens[0]);
		}
	}

	/**
	 * e.g. .half 1,2,3
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void half(Token[] tokens) throws ParserException {
		int i = 0;
		try {
			i = 1;
			do {
				int value = Integer.decode(tokens[i++].getString());
				memory_.writeHalf(segmentPointer_.get(), (short) value);
				segmentPointer_.add(2);
				memory_.setDataEnd(segmentPointer_.get());
			} while (i < tokens.length && tokens[i++].getString().equals(","));
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i - 1]);
		}
	}

	/**
	 * e.g. .space 4,2,2
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void space(Token[] tokens) throws ParserException {
		int i = 0;
		try {
			i = 1;
			do {
				int value = Integer.decode(tokens[i++].getString());
				segmentPointer_.add(value);
			} while (i < tokens.length && tokens[i++].getString().equals(","));
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i - 1]);
		}
	}

	/**
	 * e.g. .text 0x100
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void text(Token[] tokens) throws ParserException {
		segmentPointer_ = textPointer_;
		if (tokens.length == 1) {
			return;
		} else if (tokens.length == 2) {
			try {
				int value = Integer.decode(tokens[1].getString());
				if (value < 0)
					throw new ParserException(NUMBER_NEGATIVE, tokens[1]);
				textPointer_.set(value);
				// TODO what happens when multiple .text directives are found?
				memory_.setTextBegin(value);
			} catch (NumberFormatException ex) {
				throw new ParserException(NOT_A_NUMBER, tokens[1]);
			}
		} else {
			throw new ParserException(UNEXPECTED_TRASH, tokens[2]);
		}
	}

	/**
	 * e.g. .word 1,2,3
	 * 
	 * @param tokens
	 * @throws ParserException
	 */
	private void word(Token[] tokens) throws ParserException {
		int i = 0;
		try {
			i = 1;
			do {
				int value = Integer.decode(tokens[i++].getString());
				memory_.writeWord(segmentPointer_.get(), value);
				segmentPointer_.add(4);
				memory_.setDataEnd(segmentPointer_.get());
			} while (i < tokens.length && tokens[i++].getString().equals(","));
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ParserException(INCOMPLETE_DIRECTIVE, tokens[0]);
		} catch (NumberFormatException ex) {
			throw new ParserException(NOT_A_NUMBER, tokens[i - 1]);
		}
	}

	/*
	 * ========================================================================
	 */
	private String replaceEscapeSequences(String str) throws ParserException {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (c != '\\')
				buffer.append(c);
			else {
				try {
					++i;
					switch (str.charAt(i)) {
					case '0':
						buffer.append('\0');
						break;
					case 'n':
						buffer.append('\n');
						break;
					case 't':
						buffer.append('\t');
						break;
					case 'f':
						buffer.append('\f');
						break;
					case 'b':
						buffer.append('\b');
						break;
					case '\\':
						buffer.append('\\');
						break;
					case '\0':
						buffer.append('\0');
						break;
					case '\'':
						buffer.append('\'');
						break;
					case '\"':
						buffer.append('\"');
						break;
					case 'x':
						++i;
						char x1 = str.charAt(i);
						++i;
						char x2 = str.charAt(i);
						byte hex = Byte.decode("0x" + x1 + x2);
						buffer.append((char) hex);
						break;
					default:
						buffer.append(c);
						buffer.append(str.charAt(i));
					}
				} catch (IndexOutOfBoundsException ex) {
					throw new ParserException(UNEXPECTED_LITERAL_END, null);
				} catch (NumberFormatException ex) {
					throw new ParserException(NOT_A_NUMBER, null);
				}
			}
		}
		return buffer.toString();
	}
}
