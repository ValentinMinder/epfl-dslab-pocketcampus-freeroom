package org.pocketcampus.tool.thriftcompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

	private FileUtils() {}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static final void copy( File source, File destination ) throws IOException {
		if( source.isDirectory() ) {
			copyDirectory( source, destination );
		} else {
			copyFile( source, destination );
		}
	}
	
	public static final void copyDirectory( String source, String destination ) throws IOException {
		copyDirectory(new File(source), new File(destination));
	}
	
	
	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static final void copyDirectory( File source, File destination ) throws IOException {
		if( !source.isDirectory() ) {
			throw new IllegalArgumentException( "Source (" + source.getPath() + ") must be a directory." );
		}

		if( !source.exists() ) {
			throw new IllegalArgumentException( "Source directory (" + source.getPath() + ") doesn't exist." );
		}
		
		if(source.getPath().endsWith(".svn")) {
			// skip .svn directories
			return;
		}

//		if( destination.exists() ) {
//			throw new IllegalArgumentException( "Destination (" + destination.getPath() + ") exists." );
//		}

		destination.mkdirs();
		File[] files = source.listFiles();

		for( File file : files ) {
			if( file.isDirectory() ) {
				copyDirectory( file, new File( destination, file.getName() ) );
			} else {
				copyFile( file, new File( destination, file.getName() ) );
			}
		}
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static final void copyFile( File source, File destination ) throws IOException {
		FileChannel sourceChannel = new FileInputStream( source ).getChannel();
		FileChannel targetChannel = new FileOutputStream( destination ).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
		sourceChannel.close();
		targetChannel.close();
	}
	
	public static boolean deleteDir(String dir) {
		return deleteDir(new File(dir));
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	        	if(".svn".equals(children[i])) // do not delete .svn folders
	        		continue;
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

	public static void clearDir(String directory) {
		deleteDir(directory);
		new File(directory).mkdir();
	}
}