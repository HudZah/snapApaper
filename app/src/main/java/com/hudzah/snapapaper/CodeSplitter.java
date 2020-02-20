package com.hudzah.snapapaper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hudzah.snapapaper.TypeActivity.paperNumberEditText;
import static com.hudzah.snapapaper.TypeActivity.selectedExamCode;

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

    String paperNumber;

    List<String> urlsToDownload = new ArrayList<String>();

    List<String> papersToDownload = new ArrayList<String>();

    String newPaperNumber;

    String splitVariant;


    private static final String TAG = "CodeSplitter";

    public CodeSplitter(Context context, String codeText){

        this.context = context;
        this.codeText = codeText;
    }


    public void createCodeForType(){

        clearList();

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

                if(Integer.parseInt(splitText[1]) > 10){

                    splitText[1] = splitText[1].substring(0, 1);
                    Log.i("YearBelow", "Year below 2009 is " + splitText[1]);

                }else{

                    splitText[1] = splitText[1].substring(1);
                    Log.i("YearBelow", "Year below 2009 is " + splitText[1]);
                }


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

    public void createCodeForSearch() {

        clearList();

        String examLevel = TypeActivity.spinnerExamLevel.getSelectedItem().toString();

        paperCode = selectedExamCode + "_";

        String examYear = TypeActivity.spinnerYear.getSelectedItem().toString().substring(2);

        String paperSession = TypeActivity.spinnerSession.getSelectedItem().toString();


        if (paperSession.equals("Feb/Mar")) {

            paperCode = paperCode + "m" + examYear;

        } else if (paperSession.equals("May/Jun")) {

            paperCode = paperCode + "s" + examYear;

        } else if (paperSession.equals("Oct/Nov")) {


            paperCode = paperCode + "w" + examYear;
        }

        if (Integer.parseInt(examYear) < 10) {
            if(paperSession.equals("Oct/Nov") && Integer.parseInt(examYear) == 9) {

                paperNumber = paperNumberEditText.getText().toString();
            }
            else {
                paperNumber = paperNumberEditText.getText().toString().substring(0, 1);
                Log.i("YearBelow", "Year below 2009 is " + paperNumber);
            }

        } else {

            paperNumber = paperNumberEditText.getText().toString();
        }

        paperCodeMs = paperCode + "_ms_" + paperNumber;

        paperCode = paperCode + "_qp_" + paperNumber;

        pdfUrlPart = MainActivity.examCodesMap.get(selectedExamCode);
        subjectName = MainActivity.examCodesMap.get(selectedExamCode);

        if (pdfUrlPart != null) {

            pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

            Log.i(TAG, "Exam subject name: " + pdfUrlPart);


            if (Integer.valueOf(selectedExamCode) > 8000) {

                examLevel = "A%20Levels";
                examLevelFull = "A Level";
            } else if (Integer.valueOf(selectedExamCode) < 1000) {

                examLevel = "IGCSE";
                examLevelFull = "IGCSE";
            } else {

                examLevel = "O%20Levels";
                examLevelFull = "O level";
            }

            pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

            pdfUrlMs = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCodeMs + ".pdf";

            Log.i(TAG, pdfUrl + "MS IS " + pdfUrlMs);

            urlsToDownload.add(pdfUrl);
            urlsToDownload.add(pdfUrlMs);

            papersToDownload.add(paperCode);
            papersToDownload.add(paperCodeMs);
        }
    }

    public void createCodeForFullSet(int val, String paperType){

        clearList();

        paperCode = getPaperCode();

        Log.i("Worked", paperCode);

        // 9701_w16_qp_42

        String[] splitCode = paperCode.split("_");

        if(Integer.parseInt(splitCode[3]) > 10){

            splitVariant = splitCode[3].substring(1);

        }
        else{

            splitVariant = "";
        }



        Log.i("Worked", splitVariant);
        String[] filenamesFullSet = new String[val];
        String[] urlsToDownloadFullSet = new String[val];

        for(int i = 1; i < val + 1; i++){

            Log.i("Worked", splitCode[3]);

            paperNumber = i + splitVariant;
            Log.i("Worked", paperNumber);

            String newPaperCode = splitCode[0] + "_" + splitCode[1] + paperType + paperNumber;

            String newPdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + newPaperCode + ".pdf";

            papersToDownload.add(newPaperCode);

            urlsToDownload.add(newPdfUrl);

//            filenamesFullSet[i -1] = newPaperCode;
//
//
//            urlsToDownloadFullSet[i -1] = newPdfUrl;

            Log.i("Worked",  urlsToDownload+ "Paper codes are " + papersToDownload);
        }
    }

    public void createCodeForMultipleYears(int val, String paperType, int fromYear, int toYear){

        clearList();

        paperCode = getPaperCode();

        String[] filenamesMultiple = new String[val + 1];

        String[] splitCode = paperCode.split("_");

        String paperSeason = splitCode[1].substring(0, 1); //w

        String paperYear = splitCode[1].substring(1); //16

        String[] urlsToDownloadMultiple = new String[val + 1];

        int nextYear = fromYear;

        for (int i = 0; i < val + 1; i++) {


            if(nextYear < 2010){

                if(nextYear == 2009 && splitCode[1].substring(0,1).equals("w")){

                    if(Integer.parseInt(splitCode[3]) > 1) {

                        newPaperNumber = splitCode[3];
                    }else{

                        newPaperNumber = splitCode[3] + 2;
                    }

                }else {

                    if(Integer.parseInt(splitCode[3]) > 10) {

                        newPaperNumber = splitCode[3].substring(0, 1);
                    }
                    else{

                        newPaperNumber = splitCode[3];
                    }
                }

            }
            else{

                if(Integer.parseInt(splitCode[3]) > 10) {

                    newPaperNumber = splitCode[3];
                }
                else{

                    newPaperNumber = splitCode[3] + 2;
                }
            }

            //paperYears[i] = String.valueOf(nextYear).substring(2);

            Log.i("PaperYear", String.valueOf(nextYear));



            String newPaperYear = paperSeason + nextYear;

            Log.i("PaperYear", newPaperNumber);

            newPaperYear = paperSeason + newPaperYear.substring(3);

            Log.i("PaperYear", newPaperYear);

            String newPaperCode = splitCode[0] + "_" + newPaperYear + paperType + newPaperNumber;

            Log.i("PaperYear", newPaperCode);

            filenamesMultiple[i] = newPaperCode;

            String newPdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + newPaperCode + ".pdf";

            urlsToDownloadMultiple[i] = newPdfUrl;

            nextYear = nextYear + 1;

            papersToDownload.add(newPaperCode);
            urlsToDownload.add(newPdfUrl);


        }
    }


    public List<String> getUrls(){

        return urlsToDownload;
    }

    public List<String> getCodes(){

        return papersToDownload;
    }

    public String getPaperCode(){

        return paperCode;
    }

    public void clearList(){

        papersToDownload.clear();
        urlsToDownload.clear();
    }
}
