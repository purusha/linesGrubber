# LinesGrubber - Selective Line Extractor from Files

## Overview
LinesGrubber is a Java command-line application designed to extract specific lines from multiple text files based on a configuration file (proto file). The software allows selective content gathering from a directory structure and consolidates it into a single output file.

## Core Features

### 1. **Command Line Interface**
- Uses PicoCLI library to handle parameters and options
- Main command: `grub`
- Two mandatory parameters:
  - `-r, --root`: Root directory containing files to process
  - `-p, --proto`: Configuration file specifying which files and lines to extract

### 2. **Configuration File Parsing (Proto File)**
The proto file defines extraction logic through a structured format:

#### **Supported Format:**
- **File paths**: One line per file to process (relative path to root directory)
- **Line specification**: Subsequent lines define which lines to extract from the previous file
  - Single line format: `number` (extracts from specified line to end of file)
  - Range format: `start,end` (extracts lines from start to end inclusive)
  - Empty lines are ignored

#### **Proto File Example:**
```
src/main/java/Example.java
1,10
15,20
config/application.properties
5
```

### 3. **Intelligent Range Management**
- **Automatic merge**: Overlapping ranges are automatically unified to optimize extraction
- **Sorting**: Ranges are sorted to ensure sequential extraction
- **Validation**: Correctness checking of line numbers and ranges

### 4. **Extraction and Consolidation**
- **Selective reading**: Extracts only specified lines without loading entire file into memory
- **UTF-8 encoding**: Full support for Unicode characters
- **Unified output**: All extracted content is consolidated into a single output file
- **Separators**: Adds separators between different extracts to maintain readability

### 5. **Validation and Security Controls**
- **Existence verification**: Checks that all specified files actually exist
- **Read permission checks**: Verifies read permissions for root directory and proto file
- **Path validation**: Constructs and validates absolute paths of target files
- **Error handling**: Detailed error reporting during processing

### 6. **Output File Management**
- **Unique naming**: Automatically generates file name using UUID to avoid conflicts
- **Automatic creation**: Creates output file in current directory
- **Append mode**: Uses append mode to progressively build the resulting file

## Technical Architecture

### **Core Components:**
1. **App.java**: Entry point and command line management
2. **ProtoParser.java**: Configuration file parser and range management
3. **ProtoHandler.java**: Processing engine and line extraction

### **Dependencies Used:**
- **PicoCLI**: Command line interface management
- **Apache Commons Lang3**: String manipulation and range utilities
- **Lombok**: Boilerplate code reduction with annotations
- **Java NIO**: Efficient file handling and encoding

## Typical Use Cases

### **Documentation Extraction**
Consolidate specific sections from scattered documentation files in a project

### **Code Review and Analysis**
Extract specific methods or classes from multiple source files for review or analysis

### **Report Generation**
Collect log entries or specific configurations from multiple files

### **Template Generation**
Assemble code sections from different template files

## Operational Limitations
- Works exclusively on text files
- Line numbers in proto file are 1-based (start from 1)
- Output file is created in current execution directory
- Does not support pattern matching on file names (requires explicit paths)

## Output and Logging
- Displays extraction configuration before processing
- Shows generated output file path
- Provides feedback on individual file read errors
- Output format: extracted contents separated by newlines

The software is fully functional and ready for production use, with robust error handling and complete input validation.
