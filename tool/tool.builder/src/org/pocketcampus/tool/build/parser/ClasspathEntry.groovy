package org.pocketcampus.tool.build.parser

class ClasspathEntry implements Comparable<ClasspathEntry> {
	private String mKind;
	private String mSrc;
	
	ClasspathEntry(String kind, String src) {
		mKind = kind;
		mSrc = src;	
	}
	
	String getKind() {return mKind;}
	String getSrc() {return mSrc;}
	void setSrc(String src) {mSrc = src;}
	
	@Override
	public String toString() {
		return mKind + ": " + mSrc;
	}
	
	@Override
	boolean equals(other) {
		if(mKind.equals(other.getKind())) {
			if(mSrc.equals(other.getSrc())) {
				return true
			}
		}
		
		return false
	}
	
	@Override
	public int hashCode() {
		return mKind.hashCode() * mSrc.hashCode()
	}
	
	@Override
	int compareTo(other) {
		return toString().compareTo(other.toString())
	}
}
