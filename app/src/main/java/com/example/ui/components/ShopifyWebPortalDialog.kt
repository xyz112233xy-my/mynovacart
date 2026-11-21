package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.WebPortalRequest

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShopifyWebPortalDialog(
  portalRequest: WebPortalRequest?,
  onOrderCompleted: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (portalRequest == null) return

  val context = LocalContext.current
  var progress by remember(portalRequest.url) { mutableIntStateOf(0) }
  var currentUrl by remember(portalRequest.url) { mutableStateOf(portalRequest.url) }
  var orderRecorded by remember(portalRequest.url) { mutableStateOf(false) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier = Modifier.fillMaxSize().systemBarsPadding(),
      color = PureWhite,
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Outlined.Close, contentDescription = "Close checkout", tint = ObsidianInk)
          }

          Column(
            modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = portalRequest.title,
              style = MaterialTheme.typography.titleSmall,
              color = ObsidianInk,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = EmeraldLive,
                modifier = Modifier.size(12.dp),
              )
              Text(
                text = Uri.parse(currentUrl).host ?: "mynovacart.in",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }

          Row {
            IconButton(onClick = { webViewRef?.reload() }) {
              Icon(Icons.Outlined.Refresh, contentDescription = "Reload page", tint = ObsidianInk)
            }
            IconButton(
              onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                runCatching { context.startActivity(intent) }
              }
            ) {
              Icon(
                Icons.Outlined.OpenInBrowser,
                contentDescription = "Open in browser",
                tint = ObsidianInk,
              )
            }
          }
        }

        if (progress in 1..99) {
          LinearProgressIndicator(
            progress = { progress / 100f },
            color = NovaCoral,
            modifier = Modifier.fillMaxWidth(),
          )
        } else {
          HorizontalDivider(color = HairlineBorder)
        }

        AndroidView(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          factory = { ctx ->
            WebView(ctx).apply {
              CookieManager.getInstance().setAcceptCookie(true)
              CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
              settings.javaScriptEnabled = true
              settings.domStorageEnabled = true
              settings.loadWithOverviewMode = true
              settings.useWideViewPort = true
              settings.javaScriptCanOpenWindowsAutomatically = true

              webChromeClient =
                object : WebChromeClient() {
                  override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                  }
                }

              webViewClient =
                object : WebViewClient() {
                  override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (!url.isNullOrBlank()) {
                      currentUrl = url
                      checkThankYouUrl(url)
                    }
                  }

                  override fun onPageFinished(view: WebView?, url: String?) {
                    if (!url.isNullOrBlank()) {
                      currentUrl = url
                      checkThankYouUrl(url)
                    }
                  }

                  override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                  ): Boolean {
                    val targetUri = request?.url ?: return false
                    val scheme = targetUri.scheme?.lowercase().orEmpty()
                    if (scheme == "http" || scheme == "https") {
                      return false
                    }
                    // Route UPI, PhonePe, GPay, Paytm, and intent:// payment links to installed apps
                    return runCatching {
                      if (scheme == "intent") {
                        val parsed = Intent.parseUri(targetUri.toString(), Intent.URI_INTENT_SCHEME)
                        ctx.startActivity(parsed)
                      } else {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, targetUri))
                      }
                      true
                    }.getOrDefault(false)
                  }

                  private fun checkThankYouUrl(url: String) {
                    if (
                      portalRequest.isCheckout &&
                        !orderRecorded &&
                        (url.contains("/thank_you", ignoreCase = true) ||
                          url.contains("/thank-you", ignoreCase = true) ||
                          url.contains("/orders/", ignoreCase = true))
                    ) {
                      orderRecorded = true
                      onOrderCompleted(url)
                    }
                  }
                }

              loadUrl(portalRequest.url)
              webViewRef = this
            }
          },
          update = { webView ->
            webViewRef = webView
          },
        )
      }
    }
  }
}
