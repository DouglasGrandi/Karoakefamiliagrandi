# Karoakefamiliagrandi (Android TV/Box)

App de karaokê para Android TV/TV Box com fila de músicas do YouTube, captura de microfone e pontuação simples (energia + estabilidade de pitch). Reprodução via app oficial do YouTube (Intent) para respeitar ToS.

## Recursos
- Cola link do YouTube ou ID (extrator de ID embutido)
- Fila de músicas (adicionar/remover)
- Pontuação: energia (RMS) + estabilidade (pitch)
- UX para controle remoto (D-Pad)
- Overlay: contagem 3-2-1, VU, placar parcial

## Requisitos
- Android Studio Giraffe+
- SDK 24+ (Android 7.0+)
- Android TV/Box com microfone (USB/Bluetooth)

## Build rápido
```bash
./gradlew assembleDebug
# APK em: app/build/outputs/apk/debug/app-debug.apk
```

## Instalar na TV Box
Ative fontes desconhecidas e instale o APK gerado. Ou use `adb install`.

## Observação legal
Não baixe/empacote conteúdo do YouTube. Use somente reprodução via app oficial ou navegador.

## Licença
MIT — veja `LICENSE`.
