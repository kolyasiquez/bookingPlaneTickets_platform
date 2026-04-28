package com.airline.util;

import com.airline.model.Reservation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class TicketQrCodeGenerator {

    public static byte[] generateQRCode(Reservation reservation) {
        try {
            String qrText = "E-TICKET\n" +
                    "Reservation ID: " + reservation.getId() + "\n" +
                    "Passenger: " + reservation.getPassengerName() + "\n" +
                    "Flight: " + reservation.getFlight().getCityFrom() + " -> " + reservation.getFlight().getCityTo() + "\n" +
                    "Date: " + reservation.getFlight().getDate() + " " + reservation.getFlight().getTime() + "\n" +
                    "Price: $" + reservation.getFlight().getPrice();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
