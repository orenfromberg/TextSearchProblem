package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSearcher {

	//	class to represent a token in the data
	private class Token {
		private Integer _startPosition;
		private Integer _endPosition;
		private Integer _index;
		
		/**
		 * @return the _index
		 */
		public Integer get_index() {
			return _index;
		}

		/**
		 * @param _index the _index to set
		 */
		public void set_index(Integer _index) {
			this._index = _index;
		}

		public Token(String token, Integer startPosition, Integer endPosition, Integer index) {
			setStartPosition(startPosition);
			setEndPosition(endPosition);
			set_index(index);
		}

		/**
		 * @return the startPosition
		 */
		public Integer getStartPosition() {
			return _startPosition;
		}

		/**
		 * @param startPosition the startPosition to set
		 */
		public void setStartPosition(Integer startPosition) {
			this._startPosition = startPosition;
		}

		/**
		 * @return the endPosition
		 */
		public Integer getEndPosition() {
			return _endPosition;
		}

		/**
		 * @param endPosition the endPosition to set
		 */
		public void setEndPosition(Integer endPosition) {
			this._endPosition = endPosition;
		}
	}

	// string data that we are searching through
	private String _data;
	
	// string data represented as an array of tokens 
	private List<Token> _tokenArray = new ArrayList<Token>();
	
	// hashmap that maps token string to Token object
	private HashMap<String, List<Token>> _tokenMap = new HashMap<String, List<Token>>();

	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;
		
		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}
		
		init(w.toString());
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	protected void init(String fileContents) {
		// TODO -- fill in implementation

		// copy the contents of the file into data.
		_data = fileContents;
		
		// this is the regex pattern used to match a word.
		String pattern = "([A-Za-z0-9\\']+)";
				
		// create a regex pattern
		Pattern p = Pattern.compile(pattern);
		
		// create a matcher for the regex
		Matcher m = p.matcher(_data);
		
		// iterate over matches and add to tokenArray.
		for (int i = 0; m.find(); i++) {
			Token t = new Token(m.group(), m.start(), m.end(), i);
			_tokenArray.add(t);

			String tokenLowerCase = m.group().toLowerCase();
			if (_tokenMap.containsKey(tokenLowerCase)) {
				_tokenMap.get(tokenLowerCase).add(t);
			}
			else {
				List<Token> l = new ArrayList<Token>();
				l.add(t);
				_tokenMap.put(tokenLowerCase, l);
			}
			
		}
	}
	
	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
		// TODO -- fill in implementation
		
		// check the token map
		List<Token> tokenList = _tokenMap.getOrDefault(queryWord.toLowerCase(), new ArrayList<Token>(0));
		
		String[] results = new String[tokenList.size()];
		
		for (int i = 0; i < tokenList.size(); i++) {
			results[i] = getMatchContextByIndex(tokenList.get(i).get_index(), contextWords);
		}
		
		return results;
	}

	// method to index into the tokenArray and return the surrounding context.
	private String getMatchContextByIndex(Integer index, int contextWords) {
		
		// get starting token index.
		Integer startIndex = Math.max(index - contextWords, 0);
		
		// get ending token index.
		Integer endIndex = Math.min(index + contextWords, _tokenArray.size() - 1);
		
		// get index of substring start.
		// return any punctuation that appears before the first word.
		Integer start = startIndex == 0 ? 0 :_tokenArray.get(startIndex).getStartPosition();
		
		// get index of substring end.
		// return any punctuation that appears after the last word.
		Integer end = endIndex == _tokenArray.size() - 1 ? _data.length() : _tokenArray.get(endIndex).getEndPosition();
		
		return _data.substring(start, end);
	}

}

// Any needed utility classes can just go in this file

