package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;

public class ShowPdfActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);

        WebView webView = findViewById(R.id.webview_pdf);
        progressBar = findViewById(R.id.progress_bar);

        // Habilita o JavaScript no WebView (caso necessário)
        webView.getSettings().setJavaScriptEnabled(true);

        // Configura o WebView para abrir o PDF
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            // Mostra o ProgressBar enquanto o PDF estiver carregando
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Carrega o PDF diretamente do link "raw" do GitHub
        webView.loadUrl("https://drive.google.com/file/d/1DuVOVujWOBLei4fdAwL3Qlr25WANfIRu/view");
    }
}
