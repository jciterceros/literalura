package com.jciterceros.literalura.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class ConsumoAPI {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public String obterDados(String url) {
        try {
            System.out.println("Fazendo requisição para: " + url);

            // Criar request com headers completos de navegador
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Pragma", "no-cache")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String body = response.body() != null ? response.body().string() : "";

                System.out.println("Status da resposta: " + response.code() + " " + response.message());
                // System.out.println("Headers da resposta: " + response.headers());
                System.out.println("Resposta recebida com sucesso. Tamanho: " + body.length());
                // System.out.println(body);

                return body;
            }
        } catch (IOException e) {
            System.err.println("Erro ao fazer requisição: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro na requisição HTTP", e);
        }
    }
}