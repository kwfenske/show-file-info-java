/*
  Show File Information #2 - Show Standard Java Information About Files
  Written by: Keith Fenske, http://kwfenske.github.io/
  Wednesday, 10 August 2022
  Java class name: ShowFileInfo2
  Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL.

  This is a Java 1.4 console application to show standard information about
  files according to the Java run-time environment: name, size, date, etc.
  None of this would be necessary if current operating systems didn't try to be
  overly friendly, for example, saying that a file was modified "23 minutes
  ago" instead of giving the actual time of day (where the "23 minutes ago"
  text could be appended in parentheses).  Put file names or paths on the
  command line.  Example:

      java  ShowFileInfo2  *.txt

  There is no graphical interface (GUI) for this program; it must be run from a
  command prompt, command shell, or terminal window.  The only option is a help
  summary.

  Apache License or GNU General Public License
  --------------------------------------------
  ShowFileInfo2 is free software and has been released under the terms and
  conditions of the Apache License (version 2.0 or later) and/or the GNU
  General Public License (GPL, version 2 or later).  This program is
  distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the license(s) for more details.  You should have
  received a copy of the licenses along with this program.  If not, see the
  http://www.apache.org/licenses/ and http://www.gnu.org/licenses/ web pages.
*/

import java.io.*;                 // standard I/O
import java.text.*;               // number formatting
import java.util.*;               // calendars, dates, lists, maps, vectors

public class ShowFileInfo2
{
  /* constants */

  static final String COPYRIGHT_NOTICE =
    "Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL.";
  static final int EXIT_FAILURE = -1; // incorrect request or errors found
  static final int EXIT_SUCCESS = 1; // request completed successfully
  static final int EXIT_UNKNOWN = 0; // don't know or nothing really done
  static final String PROGRAM_TITLE =
    "Show Standard Java Information About Files - by: Keith Fenske";

  /* class variables */

  static NumberFormat formatComma; // format with commas (digit grouping)
  static SimpleDateFormat formatDateDst; // format local time with DST
  static SimpleDateFormat formatDateGmt; // format in GMT time zone
  static SimpleDateFormat formatDateLocal; // format in local time zone
  static boolean mswinFlag;       // true if running on Microsoft Windows
  static TimeZone ourTimeZone;    // our local time zone
  static int totalFiles;          // number of files found
  static int totalFolders;        // number of folders found (directories)

/*
  main() method

  We run as a console application.  There is no graphical interface.
*/
  public static void main(String[] args)
  {
    int i;                        // index variable
    String word;                  // one parameter from command line

    /* Initialize global and local variables. */

    mswinFlag = System.getProperty("os.name").startsWith("Windows");
    totalFiles = totalFolders = 0; // no files or folders found yet

    /* Initialize number and date formatting styles. */

    formatComma = NumberFormat.getInstance(); // current locale
    formatComma.setGroupingUsed(true); // use commas or digit groups
    formatDateDst = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    formatDateGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
    formatDateGmt.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
    formatDateLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)");
    ourTimeZone = formatDateLocal.getTimeZone(); // get local time zone

    /* Check command-line parameters for options.  Anything we don't recognize
    as an option is assumed to be a file or folder name. */

    for (i = 0; i < args.length; i ++)
    {
      word = args[i].toLowerCase(); // easier to process if consistent case
      if (word.length() == 0)
      {
        /* Ignore empty parameters, which are more common than you might think,
        when programs are being run from inside scripts (command files). */
      }

      else if (word.equals("?") || word.equals("-?") || word.equals("/?")
        || word.equals("-h") || (mswinFlag && word.equals("/h"))
        || word.equals("-help") || (mswinFlag && word.equals("/help")))
      {
        showHelp();               // show help summary
        System.exit(EXIT_UNKNOWN); // exit application after printing help
      }

      else if (word.startsWith("-") || (mswinFlag && word.startsWith("/")))
      {
        System.err.println();     // blank line
        System.err.println("Option not recognized: " + args[i]);
        showHelp();               // show help summary
        System.exit(EXIT_FAILURE); // exit application after printing help
      }

      else                        // assume file or folder name
        doFileOrFolder(args[i]);  // pass name in original case
    }

    /* Give a summary of how many files or folders were found. */

    if ((totalFiles > 0) || (totalFolders > 0))
    {
      System.out.println();       // blank line
      System.out.println("Found " + totalFiles + " files and " + totalFolders
        + " folders.");
      System.exit(EXIT_SUCCESS);
    }
    else                          // no files or folders on command line
    {
      showHelp();                 // show help summary
      System.exit(EXIT_UNKNOWN);  // exit application after printing help
    }

  } // end of main() method


/*
  doFileOrFolder() method

  Show information about one file or folder, if it exists.
*/
  static void doFileOrFolder(String givenName)
  {
    File canonFile;               // full directory resolution of <givenFile>
    File givenFile;               // file or folder given by user
    int i;                        // index variable

    /* Try to find the correct name for this file or folder. */

    givenFile = new File(givenName); // get Java File object for argument
    try { canonFile = givenFile.getCanonicalFile(); } // resolve file name
    catch (IOException ioe) { canonFile = givenFile; } // accept abstract

    /* Consider three cases: file, folder, or neither. */

    System.out.println();         // blank line

    if (canonFile.isDirectory())  // exists, is folder, assume not file
    {
      totalFolders ++;            // one more folder found
      System.out.println("     folder name: " + canonFile.getName());
      System.out.println("       full path: " + canonFile.getPath());

      /* Show the number of files (with total size) and subfolders. */

      File[] contents = canonFile.listFiles(); // unsorted, no filter
      if (contents == null)       // happens for protected folders
      {
        System.out.println("        contents: unknown, protected by system");
      }
      else                        // normal folder, look at contents
      {
        long folderBytes = 0;     // no file sizes found yet
        long folderFiles = 0;     // no files found yet
        long folderSubfolders = 0; // no subfolders found yet
        for (i = 0; i < contents.length; i ++)
        {
          if (contents[i].isDirectory()) // is this a subfolder in folder?
            folderSubfolders ++;
          else if (contents[i].isFile()) // is this a file in folder?
          {
            folderBytes += contents[i].length(); // more bytes in this folder
            folderFiles ++;       // one more file in this folder
          }
          else { /* ignore anything else */ }
        }
        System.out.println("        contains: "
          + formatComma.format(folderFiles) + " files with "
          + formatComma.format(folderBytes) + " bytes and "
          + formatComma.format(folderSubfolders) + " subfolders");
        printDateTime(canonFile); // common code for both files and folders
      }
    }

    else if (canonFile.isFile())  // exists, is file, not folder
    {
      totalFiles ++;              // one more file found
      System.out.println("       file name: " + canonFile.getName());
      System.out.println("       in folder: " + canonFile.getParent());
      System.out.println("    size (bytes): "
        + formatComma.format(canonFile.length()) + " or 0x"
        + Long.toHexString(canonFile.length()));
      printDateTime(canonFile);   // common code for both files and folders
    }

    else                          // does not exist, or not file or folder
    {
      System.out.println("Not a file or folder: " + givenName);
    }

  } // end of doFileOrFolder() method


/*
  printDateTime() method

  Show a file or folder's last modification date and time in various formats,
  starting with the Java time stamp in milliseconds since 1970.  When a Java
  time stamp is exactly zero, it usually means that the operating system is
  protecting a file or folder.  Since we accept values before and after this,
  we don't do anything special for a value of zero.
*/
  static void printDateTime(File givenFile)
  {
    long javaTimeStamp = givenFile.lastModified();
                                  // Java date and time in milliseconds
    System.out.println(" Java time stamp: "
      + formatComma.format(javaTimeStamp));
    Date fileDateTime = new Date(javaTimeStamp);
                                  // convert millis to Java Date object
    System.out.println("   GMT time zone: "
      + formatDateGmt.format(fileDateTime));
    System.out.println(" local time zone: "
      + formatDateLocal.format(fileDateTime));

    /* Microsoft Windows adjusts file dates and times using the current rules
    for daylight saving time (DST), no matter which rules should be applied at
    that actual date and time.  Correcting for this assumption is almost
    impossible because both Java and Windows think they are in charge of time
    zones and DST.  The following code only does local times, and is quite
    likely to break if either the JRE or Windows changes. */

    long dstMillis = javaTimeStamp; // start with no DST correction
    dstMillis -= ourTimeZone.getOffset(dstMillis)
      - ourTimeZone.getOffset((new Date()).getTime());
    System.out.println("  adjust for DST: "
      + formatDateDst.format(new Date(dstMillis)));

    /* Is the file or folder hidden?  Can we read or write?  The caller tested
    Java 1.4 attributes for isDirectory() and isFile(), and hence we know that
    exists() is also true. */

    System.out.println("      attributes: hidden " + givenFile.isHidden()
      + ", read " + givenFile.canRead()
      + ", write " + givenFile.canWrite());

  } // end of printDateTime() method


/*
  showHelp() method

  Show the help summary.  This is a UNIX standard and is expected for all
  console applications, even very simple ones.
*/
  static void showHelp()
  {
    System.err.println();
    System.err.println(PROGRAM_TITLE);
    System.err.println();
    System.err.println("  java  ShowFileInfo2  [options]  fileOrFolderNames");
    System.err.println();
    System.err.println("This is a console application.  You may give options on the command line:");
    System.err.println();
    System.err.println("  -? = -help = show summary of command-line syntax");
    System.err.println();
    System.err.println(COPYRIGHT_NOTICE);
//  System.err.println();

  } // end of showHelp() method

} // end of ShowFileInfo2 class

/* Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL. */
