echo "🔍 Verificando dependências..."
echo "Java:" && java -version 2>&1 | head -1 || echo "❌ Java não encontrado"
echo "Android SDK:" && echo ${ANDROID_HOME:-"❌ ANDROID_HOME não configurado"}
echo "ADB:" && adb version 2>/dev/null | head -1 || echo "❌ ADB não encontrado"
echo "Gradle:" && ./gradlew --version 2>/dev/null | head -1 || echo "⚠️ Gradlew não encontrado"
echo "VS Code:" && code --version | head -1 || echo "❌ VS Code não no PATH"
echo "Extensões:" && code --list-extensions | grep -E "(kotlin|java)" | head -5 || echo "⚠️ Extensões não instaladas"
