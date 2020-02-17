package com.hudzah.snapapaper;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CodeSplitter {

    Context context;

    String codeText;

    String[] splitText;

    String paperCode;

    String paperCodeMs;

    String pdfUrlPart;

    static String subjectName;

    String examLevel;

    static String examLevelFull;

    String pdfUrl;

    String pdfUrlMs;

    List<String> urlsToDownload = new ArrayList<String>();

    List<String> papersToDownload = new ArrayList<String>();


    private static final String TAG = "CodeSplitter";

    public CodeSplitter(Context context, String codeText){

        this.context = context;
        this.codeText = codeText;
    }


    public void createCodeForType(){

        splitText = codeText.split("/");


        // Splits into 9709, 42, F, M, 19
        // Splits into 9709, 42, M, J, 19
        // Splits into 9709, 42, O, N, 19



        paperCode = splitText[0] + "_";

        Log.i("Test value", splitText[0] + splitText[1]  + splitText[2] + splitText[3] + splitText[4]);

        if(Integer.parseInt(splitText[4]) < 10){
            if(splitText[3].equals("N") && Integer.parseInt(splitText[4]) == 9) {
                splitText[1] = splitText[1];
            }
            else {
                splitText[1] = splitText[1].substring(0, 1);
                Log.i("YearBelow", "Year below 2009 is " + splitText[1]);
            }
        }


        if (splitText[2].equals("F")) {

            paperCode = paperCode + "m" + splitText[4] + "_qp_" + splitText[1];

        } else if (splitText[2].equals("M")) {

            paperCode = paperCode + "s" + splitText[4] + "_qp_" + splitText[1];
        } else if (splitText[2].equals("O")) {

            paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
        } else {

            Log.i("paperCode", "Code is none");
        }


        Log.i(TAG, "Paper code is: " + paperCode);

        paperCodeMs = paperCode.replace("_qp_", "_ms_");

        pdfUrlPart = MainActivity.examCodesMap.get(splitText[0]);
        subjectName = MainActivity.examCodesMap.get(splitText[0]);

        if (pdfUrlPart != null) {

            pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

            Log.i(TAG, "Exam subject name: " + pdfUrlPart);


            if (Integer.valueOf(splitText[0]) > 8000) {

                examLevel = "A%20Levels";
                examLevelFull = "A Level";
            } else if (Integer.valueOf(splitText[0]) < 1000) {

                examLevel = "IGCSE";
                examLevelFull = "IGCSE";
            } else {

                examLevel = "O%20Levels";
                examLevelFull = "O level";
            }

            pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

            pdfUrlMs = pdfUrl.replace("_qp_", "_ms_");

            Log.i(TAG, pdfUrl + "MS IS " + pdfUrlMs);

            urlsToDownload.add(pdfUrl);
            urlsToDownload.add(pdfUrlMs);

            papersToDownload.add(paperCode);
            papersToDownload.add(paperCodeMs);
        }
    }

    public void createCodeForSearch(){


    }

    public List<String> getUrls(){

        return urlsToDownload;
    }

    public List<String> getCodes(){

        return papersToDownload;
    }
}
