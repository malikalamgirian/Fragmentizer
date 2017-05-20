/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.malikalamgirian.fyp;

import java.io.*;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Wasif
 */
public class DataSetFragmentizer {

    /*
     * Declarations
     */
    private String Input_File_URL;      /* Path of file inputted */

    private String Output_File_URL;     /* Path of file to output */

    private String Output_Folder_URL;   /* Path of output folder */

    /*
     * Maximum Number of Pair-Nodes in an output file
     */
    private int fragment_Unit;

    public DataSetFragmentizer(String Input_File_URL, int fragment_Unit) {
        /*
         * Initialization
         */
        this.Input_File_URL = Input_File_URL;
        this.fragment_Unit = fragment_Unit;
    }

    public boolean fragmentize() throws Exception {
        /*
         * 1. This fragmentizes the XML file into fragments
         * 2. Saves them in proper Documented format automatically
         * 3. returns true if successful
         */

        /*
         * Declarations
         */
        NodeList pair,              /* pair is set of Input_File_URL pairs */
                fragmentDocPair,    /* fragmentDocPair is set of Output_File_URL pairs */
                temporaryFilePair;  /* temporaryFilePair is set of inputFileCopy pairs */

        Node node;                  /* node is for importing pair nodes */

        Document doc,               /* doc is XML DOM for Input_File_URL */
                fragmentDoc,        /* fragmentDoc is XML DOM for Output_File_URL */
                temporaryFileDoc;   /* temporaryFileDoc is XML DOM for inputFileCopy */

        Element root,               /* root is DocumentElement for Input_File_URL */
                fragmentDocRoot,    /* fragmentDocRoot is DocumentElement for Output_File_URL */
                temporaryFileRoot;  /* temporaryFileRoot is DocumentElement for inputFileCopy */

        String inputFileName,       /* name of Input_File_URL file */
                inputFileCopy;      /* name of temporary copy file of Input_File_URL file */

        int fragmentNo = 0;         /* Fragment no. for folder name suffixes */


        try {
            /*
             * Here we
             * 1. Create temporary copy file "inputFileCopy" for "Input_File_URL"
             * 2. Delete all "Pair"s of "inputFileCopy"
             *    So that this can be then used by fragment Files
             * 3.
             *    3.1. Parse Inputted XML File "Input_File_URL"
             *    3.2. Select All Pair Nodes "pair"
             * 4. WHILE "pair"s are greater than "fragment_Unit"
             *    ------- THEN
             *    ------------ 4.1 make a properly Named folder
             *    ------------ 4.2 make a properly Named fragment XML file,
             *    ------------     based on temporary file "inputFileCopy"
             *    ------------ 4.3 copy fragment_Unit pairs from "pair" to the fragmentDoc
             *    ------------ 4.4 save (transform) the fragmentDoc in proper created folder
             *    ----------------------------------------------------------------------
             *    ELSE ("pair"s are less than or equal to "fragment_Unit")
             *    -------- This is the case for last fragment file
             *    ------------ 4.5 make a properly Named folder
             *    ------------ 4.6 make a properly Named fragment XML file,
             *    ------------     based on temporary file "inputFileCopy"
             *    ------------ 4.7 copy all "pair"s from "pair" to the fragmentDoc
             *    ------------ 4.8 save (transform) the fragmentDoc in proper created folder
             *    -------- END
             * 5. Remove temporary created file.
             */

            /*
             * First create Input file temporary Copy
             */
            /*
             * Get base output FolderPathName, inputted File Name Without Extension
             */
            Output_Folder_URL = FileSystemManager.getDirectoryPathNameForFileURL(Input_File_URL);
            inputFileName = FileSystemManager.getFileNameWithoutExtension(Input_File_URL);

            /* 1. Set temporary file's name */
            inputFileCopy = inputFileName + "_copy.xml";

            /* temporary file created */
            FileSystemManager.copyFile(Input_File_URL, inputFileCopy);

            /*
             * Create an XML Document for temporay created file
             */
            temporaryFileDoc = XMLProcessor.getXMLDocumentForXMLFile(inputFileCopy);

            /* Get temporaryFileRoot Document Element */
            temporaryFileRoot = temporaryFileDoc.getDocumentElement();

            /* Select all temporaryFilePair pairs */
            temporaryFilePair = temporaryFileRoot.getElementsByTagName("Pair");

            System.out.println("Total temporaryFilePair pairs prior to deletion are : " + temporaryFilePair.getLength());
            
            /* 2. Delete all the Pairs of temporary file */
            temporaryFileDoc = XMLProcessor.deleteNodeListFromXMLDocument(temporaryFileDoc, temporaryFilePair);
           
            temporaryFilePair = temporaryFileDoc.getDocumentElement().getElementsByTagName("Pair");
            
            System.out.println("Total temporaryFilePair pairs after deletion are : " + temporaryFilePair.getLength());

            /*
             * Transfer the document
             * so that, all fragments can use it
             * and add Pair nodes to it.
             */
            XMLProcessor.transformXML(temporaryFileDoc, new StreamResult(new File(inputFileCopy)));


            /* 3.1. Parse Inputted XML File */
            doc = XMLProcessor.getXMLDocumentForXMLFile(Input_File_URL);

            /* Get Document Element */
            root = doc.getDocumentElement();

            /* 3.2. Select All Pair Nodes */
            pair = root.getElementsByTagName("Pair");

            System.out.println("Total doc pairs are : " + pair.getLength());

            
            while (pair.getLength() > fragment_Unit) {
                /*
                 * This is the case where Fragment has exactly fragment_Unit
                 * nodes in it, other code-segment is out of while, where
                 * fragment has nodes less than fragment_Unit
                 */
                
                /*
                 * 4.1 Get base output FolderPathName, inputted File Name Without Extension
                 */
                Output_Folder_URL = FileSystemManager.getDirectoryPathNameForFileURL(Input_File_URL);
                inputFileName = FileSystemManager.getFileNameWithoutExtension(Input_File_URL);

                /*
                 * Create a Directory for this XML Fragment
                 */
                Output_Folder_URL += File.separatorChar + inputFileName + "_fragment_" + Integer.toString(fragmentNo);
                System.out.println("Directory to be created is : " + Output_Folder_URL);

                /*
                 * Call createDirectory
                 */
                FileSystemManager.createDirectory(Output_Folder_URL);
                /*
                 * 4.2 Set output File-Name Path URL
                 */
                Output_File_URL = Output_Folder_URL + File.separatorChar + inputFileName + ".xml";
                System.out.println("Output_File_URL to be created is : " + Output_File_URL);
                /*
                 * Create an XML Document for this fragment
                 */
                fragmentDoc = XMLProcessor.getXMLDocumentForXMLFile(inputFileCopy);

                /* Get fragmentDoc Document Element */
                fragmentDocRoot = fragmentDoc.getDocumentElement();

                System.out.println("Total doc pairs are : " + pair.getLength());

                /*
                 * 4.3 Copy fragment_Unit pairs from "pair" to the fragmentDoc
                 */
                for (int i = 0; i < fragment_Unit; i++) {

                    /*
                     * Add a particular node to fragmentDocRoot
                     */
                    node = fragmentDoc.importNode(pair.item(0).cloneNode(true), true);
                    fragmentDocRoot.appendChild(node);

                    /*
                     * Delete the particular node from doc
                     */
                    root.removeChild(pair.item(0));

                    /* Normalize doc */
                    doc.normalize();

                    System.out.println("fragmentNo : " + fragmentNo + " : Pair : " + (i+1));
                    
                }


                /* Normalize documents*/
                doc.normalize();
                fragmentDoc.normalize();

                /*
                 * 4.4 Transform XML Fragment Document
                 */
                XMLProcessor.transformXML(fragmentDoc, new StreamResult(new File(Output_File_URL)));

                /* Update fragment No. */
                ++fragmentNo;

                /* Update the pairs after deletion, and processing */
                pair = root.getElementsByTagName("Pair");
            }

            /* 
             * This is the case where "pair.getLength() <= fragment_Unit"
             */
            /*
             * 4.5 Get base output FolderPathName, inputted File Name Without Extension
             */
            Output_Folder_URL = FileSystemManager.getDirectoryPathNameForFileURL(Input_File_URL);
            inputFileName = FileSystemManager.getFileNameWithoutExtension(Input_File_URL);

            /*
             * Create a Directory for this XML Fragment
             */
            Output_Folder_URL += File.separatorChar + inputFileName + "_fragment_" + Integer.toString(fragmentNo);
            System.out.println("Directory to be created is : " + Output_Folder_URL);
            /*
             * Call createDirectory
             */
            FileSystemManager.createDirectory(Output_Folder_URL);
            /*
             * 4.6 Set output File-Name Path URL
             */
            Output_File_URL = Output_Folder_URL + File.separatorChar + inputFileName + ".xml";
            System.out.println("Output_File_URL to be created is : " + Output_File_URL);
            /*
             * Create a XML Document for this fragment
             */
            fragmentDoc = XMLProcessor.getXMLDocumentForXMLFile(inputFileCopy);

            /* Get fragmentDoc Document Element */
            fragmentDocRoot = fragmentDoc.getDocumentElement();

            /*
             * 4.7 copy all "pair"s from "pair" to the fragmentDoc
             */
            while (pair.getLength() != 0) {
                /*
                 * Add a particular node to fragmentDocRoot
                 */
                node = fragmentDoc.importNode(pair.item(0).cloneNode(true), true);
                fragmentDocRoot.appendChild(node);

                /*
                 * Delete the particular node from doc
                 */
                root.removeChild(pair.item(0));

                /* Update the pairs after deletion, and processing */
                pair = root.getElementsByTagName("Pair");
            }

            /* Normalize document */
            fragmentDoc.normalize();

            /*
             * 4.8 Transform XML Fragment Document
             */
            XMLProcessor.transformXML(fragmentDoc, new StreamResult(new File(Output_File_URL)));

            /* 
             * 5. Delete Temporary file inputFileCopy
             */
            if (FileSystemManager.deleteFile(inputFileCopy) != true)
                throw new Exception("Could not delete temporary file.");

        } catch (Exception ex) {
            throw new Exception("fragmentize got some problem : "
                    + ex + " " + ex.getMessage());
        }

        return true;

    } /* end fragmentize() */

}
