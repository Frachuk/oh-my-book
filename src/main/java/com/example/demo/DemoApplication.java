package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class, args);
        downloadBooks();

    }

    static void downloadBooks() throws IOException {

        ArrayList<String> URLS = new ArrayList<>();
        final String baseUrl = "https://link.springer.com";
        final String baseDestinationPath = "C:/pdfBooks/";

        try (Stream<String> lines = Files.lines(Paths.get("entryData.txt"))) {
            lines.forEach(URLS::add);
        }

        URLS.forEach(internalUrl -> {
            try {
                Document bodyPage = Jsoup.connect(internalUrl)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .header("Cookie", "_ga=GA1.2.1089563400.1587765106; _gid=GA1.2.431778682.1587765106; PHPSESSID=i1fmkuuh8mi6vcfqkp4ookm987")
                        .get();

                String bookTitle = bodyPage.getElementsByClass("page-title").text().replaceAll(":","");
                File savedBook = new File(baseDestinationPath + bookTitle + ".pdf");
                if (!savedBook.isFile()) {
                    try {
                        System.out.println("=================================");
                        System.out.println("Downloading... " + bookTitle);
                        String downloadUrl = baseUrl + bodyPage.getElementsByAttributeValue("title","Download this book in PDF format").get(0).attr("href");
                        URL url = new URL(downloadUrl);
                        URLConnection conn = url.openConnection();
                        if (conn.getContentType().equals("application/pdf")) {
                            InputStream in = conn.getInputStream();
                            Files.copy(in, Paths.get("C:/pdfBooks/" + bookTitle + ".pdf"));
                        }
                        System.out.println("=================================");
                        System.out.println("Download of " + bookTitle + " finish");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("=================================");
                    System.out.println(bookTitle + " already downloaded.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
