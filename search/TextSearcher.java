package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSearcher {

	//	class to represent a token in the data
	private class Token {
		private String _token;
		private Integer _startPosition;
		private Integer _endPosition;
		
		public Token(String token, Integer startPosition, Integer endPosition) {
			setToken(token);
			setStartPosition(startPosition);
			setEndPosition(endPosition);
		}

		/**
		 * @return the token
		 */
		public String getToken() {
			return _token;
		}

		/**
		 * @param token the token to set
		 */
		public void setToken(String token) {
			this._token = token;
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
		while(m.find())
			_tokenArray.add(new Token(m.group(), m.start(), m.end()));
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
		
		// pattern to match the queryWord as a regex.
		Pattern pattern = Pattern.compile(queryWord, Pattern.CASE_INSENSITIVE);
		
		// matcher to match in the data.
		Matcher m;
		
		// list of indices in tokenArray that match the queryWord
		List<Integer> matchIndices = new ArrayList<Integer>();
		
		// iterate over the tokens and if it matches, add to the matchIndices.
		for (int i = 0; i < _tokenArray.size(); i++) {
			m = pattern.matcher(_tokenArray.get(i).getToken());
			
			if (m.matches())
				matchIndices.add(i);
		}
		
		// the results to return
		String[] results = new String[matchIndices.size()];
		
		// get context for each match
		for (int i = 0; i < matchIndices.size(); i++)
			results[i] = getMatchContextByIndex(matchIndices.get(i), contextWords);
		
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

