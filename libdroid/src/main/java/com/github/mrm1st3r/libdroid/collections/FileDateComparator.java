package com.github.mrm1st3r.libdroid.collections;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator to sort files descending by their modification date.
 * 
 * @author Lukas Taake <lukas.taake@gmail.com>
 * @version 1.0.0
 */
public class FileDateComparator implements Comparator<File> {

	@Override
	public int compare(final File f1, final File f2) {
        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
    }
}
