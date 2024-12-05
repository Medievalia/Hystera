package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;

public class ShowPdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);

        WebView webView = findViewById(R.id.webview_pdf);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Habilitar JavaScript se necessário
        webView.getSettings().setJavaScriptEnabled(true);

        // Define o cliente WebView para abrir o conteúdo dentro do WebView
        webView.setWebViewClient(new WebViewClient());

        // Use WebChromeClient para gerenciar o progresso de carregamento
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);  // Exibe o ProgressBar
                } else {
                    progressBar.setVisibility(View.GONE);  // Oculta o ProgressBar quando carregado
                }
            }
        });

        // Carregar o PDF da pasta assets
        webView.loadUrl("file:///android_asset/Termos_uso.pdf");
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.webview_pdf);

        // Se o WebView puder voltar para uma página anterior, faça isso
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
