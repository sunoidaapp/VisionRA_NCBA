package com.vision.util;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class samplePdf {
  public static void main(String[] args) throws Exception {
    Document document = new Document();
    PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\pc 6\\Desktop\\RA\\2.pdf"));
    document.open();
    PdfPTable table = new PdfPTable(1);
    PdfPCell cell;
    Font fontScF = new Font();
	fontScF.setSize(7);
    Paragraph p = new Paragraph(new Phrase("center",fontScF));
    cell = new PdfPCell(p);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    
    table.addCell(cell);
    

    document.add(table);

    document.close();
  }
}