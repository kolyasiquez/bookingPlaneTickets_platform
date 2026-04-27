package com.airline.util;

import com.airline.model.Reservation;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TicketPdfGenerator {

    public static byte[] generateTicket(Reservation reservation) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("E-TICKET - " + reservation.getId());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Passenger: " + reservation.getPassengerName());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Flight: " + reservation.getFlight().getCityFrom() + " -> " + reservation.getFlight().getCityTo());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Date: " + reservation.getFlight().getDate() + " " + reservation.getFlight().getTime());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Price: $" + reservation.getFlight().getPrice());
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
