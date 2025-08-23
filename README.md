# dianGO POS - Sistema de Pagamento Android

![dianGO POS](https://img.shields.io/badge/dianGO-POS-blue.svg)
![Android](https://img.shields.io/badge/Android-21%2B-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)
![PagBank](https://img.shields.io/badge/PagBank-SDK-orange.svg)

Sistema completo de Point of Sale (POS) para Android integrado com PagBank/PagSeguro, desenvolvido em Kotlin seguindo as melhores práticas de arquitetura e segurança.

## 📋 Índice

- [Características](#-características)
- [Arquitetura](#-arquitetura)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Integração PagBank](#-integração-pagbank)
- [Testes](#-testes)
- [Deploy](#-deploy)
- [Segurança](#-segurança)
- [Troubleshooting](#-troubleshooting)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

## 🚀 Características

### Funcionalidades Principais
- ✅ **Processamento de Pagamentos**: Crédito, Débito, PIX e Vouchers
- ✅ **Dashboard Intuitivo**: Visualização de vendas do dia e transações
- ✅ **Histórico Completo**: Listagem e filtros avançados de transações
- ✅ **Sincronização**: Backup automático com servidor backend
- ✅ **Relatórios**: Exportação de dados em CSV
- ✅ **Offline First**: Funcionamento mesmo sem conexão

### Recursos Técnicos
- 🏗️ **Arquitetura MVVM** com Clean Architecture
- 🔒 **Segurança PCI-DSS** compliant
- 📱 **Material Design 3** responsivo
- 🗃️ **Armazenamento Local** com Room Database
- 🌐 **API REST** com Retrofit
- 💉 **Injeção de Dependência** com Hilt
- 🧪 **Testes Automatizados** unitários e instrumentados

## 🏗️ Arquitetura

O projeto segue a **Clean Architecture** com padrão **MVVM**:

```
┌─────────────────────────────────────┐
│           PRESENTATION              │
│  (Activities, Fragments, ViewModels)│
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│             DOMAIN                  │
│     (Use Cases, Repositories)       │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│              DATA                   │
│   (Local DB, Remote API, Models)    │
└─────────────────────────────────────┘
```

### Camadas

- **Presentation**: UI, ViewModels, Fragments
- **Domain**: Use Cases, Repository Interfaces, Models de Domínio
- **Data**: Implementação dos Repositories, Database, API Services

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia | Versão | Propósito |
|-----------|------------|---------|-----------|
| **Linguagem** | Kotlin | 1.9.20 | Linguagem principal |
| **Framework** | Android SDK | 21+ | Plataforma mobile |
| **Arquitetura** | MVVM + Clean | - | Padrão arquitetural |
| **DI** | Hilt | 2.48 | Injeção de dependência |
| **Database** | Room | 2.6.1 | Banco de dados local |
| **Network** | Retrofit + OkHttp | 2.9.0 | Cliente HTTP |
| **Async** | Coroutines | 1.7.3 | Programação assíncrona |
| **UI** | Material Design 3 | 1.11.0 | Interface do usuário |
| **Security** | EncryptedSharedPreferences | 1.1.0 | Armazenamento seguro |
| **Payment** | PagBank SDK | 1.+ | Processamento de pagamentos |
| **Testing** | JUnit + Espresso | 4.13.2 | Testes automatizados |

## 📋 Pré-requisitos

### Ambiente de Desenvolvimento
- **Android Studio**: Arctic Fox (2020.3.1) ou superior
- **JDK**: 11 ou superior
- **Android SDK**: API Level 21+ (Android 5.0)
- **Gradle**: 8.2 ou superior
- **Git**: Para controle de versão

### Conta PagBank
- Conta ativa no PagBank/PagSeguro
- Credenciais de Sandbox para testes
- Credenciais de Produção para deploy

### Hardware POS (Opcional)
- Maquininha PagBank compatível
- Android POS device certificado

## 🔧 Instalação

### 1. Clone o Repositório
```bash
git clone https://github.com/seu-usuario/diango-pos.git
cd diango-pos
```

### 2. Configure o Ambiente
```bash
# Crie o arquivo local.properties na raiz do projeto
echo "sdk.dir=/path/to/your/android/sdk" > local.properties
```

### 3. Configure as Credenciais
```bash
# Edite o build.gradle (app) com suas credenciais
# ou use variáveis de ambiente para maior segurança
export PAGBANK_EMAIL="seu-email@exemplo.com"
export PAGBANK_TOKEN="seu-token-aqui"
```

### 4. Sincronize as Dependências
```bash
./gradlew build
```

## ⚙️ Configuração

### Credenciais PagBank

#### Sandbox (Desenvolvimento)
```kotlin
// build.gradle (Module: app)
android {
    defaultConfig {
        buildConfigField "String", "PAGBANK_EMAIL", "\"diangoguerreiro@diango.com.br\""
        buildConfigField "String", "PAGBANK_TOKEN", "\"68d7956b-c147-4617-b2ad-66756770d2a5216ed16148bb965e8a61b0b037a48db68b87-2b85-4bd8-bc60-cc06ce3f43ff\""
        buildConfigField "String", "BASE_URL", "\"https://sandbox.api.pagseguro.com/\""
        buildConfigField "boolean", "IS_SANDBOX", "true"
    }
}
```

#### Produção
```kotlin
android {
    buildTypes {
        release {
            buildConfigField "String", "PAGBANK_EMAIL", "\"seu-email-producao@empresa.com\""
            buildConfigField "String", "PAGBANK_TOKEN", "\"seu-token-producao\""
            buildConfigField "String", "BASE_URL", "\"https://api.pagseguro.com/\""
            buildConfigField "boolean", "IS_SANDBOX", "false"
        }
    }
}
```

### Backend API (Opcional)
Configure a URL do seu backend em `NetworkModule.kt`:
```kotlin
@Provides
@Singleton
@BackendRetrofit
fun provideBackendRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://sua-api.com.br/") // Substituir pela URL real
        .build()
}
```

## 📁 Estrutura do Projeto

```
app/
├── src/main/java/com/diango/pos/
│   ├── data/
│   │   ├── local/              # Room Database, DAOs
│   │   ├── remote/             # API Services, Interceptors
│   │   ├── repository/         # Implementações dos Repositories
│   │   └── model/              # Data Transfer Objects
│   ├── domain/
│   │   ├── usecase/            # Casos de Uso (Business Logic)
│   │   ├── repository/         # Interfaces dos Repositories
│   │   └── model/              # Entidades de Domínio
│   ├── presentation/
│   │   ├── ui/                 # Activities, Fragments
│   │   │   ├── home/           # Tela Principal
│   │   │   ├── payment/        # Fluxo de Pagamento
│   │   │   ├── history/        # Histórico de Transações
│   │   │   └── settings/       # Configurações
│   │   ├── viewmodel/          # ViewModels
│   │   └── adapter/            # RecyclerView Adapters
│   ├── di/                     # Dependency Injection (Hilt)
│   ├── utils/                  # Utilitários e Helpers
│   └── core/                   # Componentes Core
└── src/main/res/
    ├── layout/                 # Layouts XML
    ├── values/                 # Colors, Strings, Dimensions
    ├── drawable/               # Ícones e Assets
    ├── menu/                   # Menus de Navigation
    └── navigation/             # Navigation Component
```

## 💳 Integração PagBank

### Inicialização do SDK
```kotlin
// DiangoApplication.kt
private fun initializePagBankSDK() {
    try {
        // Inicializar SDK com credenciais
        PagBankSDK.initialize(
            context = this,
            email = BuildConfig.PAGBANK_EMAIL,
            token = BuildConfig.PAGBANK_TOKEN,
            environment = if (BuildConfig.IS_SANDBOX) {
                PagBankEnvironment.SANDBOX
            } else {
                PagBankEnvironment.PRODUCTION
            }
        )
    } catch (e: Exception) {
        Log.e("PagBank", "Erro ao inicializar SDK", e)
    }
}
```

### Processamento de Transações
```kotlin
// CreatePaymentUseCase.kt
suspend operator fun invoke(
    amount: Double,
    paymentMethod: PaymentMethod,
    installments: Int = 1
): NetworkResult<Payment> {
    
    // Validações
    if (amount <= 0) {
        return NetworkResult.Error("Valor deve ser maior que zero")
    }
    
    val request = TransactionRequest(
        amount = amount,
        paymentMethod = paymentMethod.value,
        installments = installments
    )
    
    return paymentRepository.createTransaction(request)
}
```

### Tipos de Pagamento Suportados

| Método | Parcelamento | Status |
|--------|--------------|--------|
| **Cartão de Crédito** | Até 12x | ✅ Implementado |
| **Cartão de Débito** | À vista | ✅ Implementado |
| **PIX** | À vista | ✅ Implementado |
| **Voucher** | À vista | ✅ Implementado |
| **Dinheiro** | À vista | 🔄 Planejado |

## 🧪 Testes

### Executar Todos os Testes
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Testes Unitários
```bash
# Apenas testes unitários
./gradlew testDebugUnitTest

# Com relatório de cobertura
./gradlew testDebugUnitTestCoverage
```

### Testes de Integração
```bash
# Testes instrumentados
./gradlew connectedDebugAndroidTest

# Testes específicos de UI
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.diango.pos.PaymentFlowTest
```

### Estrutura de Testes
```
src/test/                           # Testes Unitários
├── java/com/diango/pos/
│   ├── viewmodel/                  # Testes de ViewModels
│   ├── usecase/                    # Testes de Use Cases
│   ├── repository/                 # Testes de Repositories
│   └── utils/                      # Testes de Utilitários

src/androidTest/                    # Testes Instrumentados
├── java/com/diango/pos/
│   ├── ui/                         # Testes de UI (Espresso)
│   ├── database/                   # Testes de Database
│   └── integration/                # Testes de Integração
```

### Exemplo de Teste Unitário
```kotlin
@Test
fun `processPayment should return success for valid amount`() = runTest {
    // Given
    val amount = 100.0
    val paymentMethod = PaymentMethod.CREDIT_CARD
    
    // When
    val result = createPaymentUseCase(amount, paymentMethod)
    
    // Then
    assertTrue(result is NetworkResult.Success)
}
```

## 🚀 Deploy

### Build de Produção
```bash
# Gerar APK assinado
./gradlew assembleRelease

# Gerar AAB para Play Store
./gradlew bundleRelease
```

### Configuração de Assinatura
```kotlin
// app/build.gradle
android {
    signingConfigs {
        release {
            storeFile file('path/to/keystore.jks')
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### CI/CD com GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    tags:
      - 'v*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Setup JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'adopt'
    
    - name: Build Release APK
      run: ./gradlew assembleRelease
      env:
        PAGBANK_EMAIL: ${{ secrets.PAGBANK_EMAIL }}
        PAGBANK_TOKEN: ${{ secrets.PAGBANK_TOKEN }}
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
    
    - name: Upload to Play Store
      uses: r0adkll/upload-google-play@v1
      with:
        serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT }}
        packageName: com.diango.pos
        releaseFiles: app/build/outputs/bundle/release/app-release.aab
```

## 🔒 Segurança

### Armazenamento Seguro
- **EncryptedSharedPreferences**: Para tokens e credenciais
- **Room Database**: Criptografia de dados sensíveis
- **Network Security Config**: HTTPS obrigatório

### Compliance PCI-DSS
- ✅ Não armazena dados de cartão
- ✅ Transmissão segura (TLS 1.2+)
- ✅ Logs sanitizados em produção
- ✅ Validação de entrada
- ✅ Auditoria de transações

### ProGuard/R8
```proguard
# Proteger classes sensíveis
-keep class com.diango.pos.data.model.** { <fields>; }
-keep class br.com.uol.pagseguro.** { *; }

# Ofuscar logs em produção
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

## 🐛 Troubleshooting

### Problemas Comuns

#### 1. Erro de Inicialização do PagBank SDK
```
ERROR: PagBank SDK initialization failed
```
**Solução**: Verifique as credenciais no `build.gradle`:
```kotlin
// Verificar se as credenciais estão corretas
buildConfigField "String", "PAGBANK_EMAIL", "\"email-correto@dominio.com\""
buildConfigField "String", "PAGBANK_TOKEN", "\"token-válido\""
```

#### 2. Transação Recusada
```
ERROR: Transaction declined - Invalid credentials
```
**Solução**: 
- Verificar se está usando o ambiente correto (Sandbox vs Produção)
- Validar credenciais no painel PagBank
- Verificar conectividade de rede

#### 3. Database Migration Error
```
ERROR: Cannot migrate database from version X to Y
```
**Solução**:
```kotlin
// Adicionar migração ou usar fallback
Room.databaseBuilder(context, DiangoPosDatabase::class.java, "database")
    .fallbackToDestructiveMigration()
    .build()
```

#### 4. Network Security Error
```
ERROR: Cleartext HTTP traffic not permitted
```
**Solução**: Verificar `network_security_config.xml`:
```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.pagseguro.com</domain>
    </domain-config>
</network-security-config>
```

### Logs Úteis

#### Habilitar Logs de Debug
```kotlin
// Em desenvolvimento
if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    okHttpClient.addInterceptor(loggingInterceptor)
}
```

#### Filtros Logcat Importantes
```bash
# Logs do PagBank
adb logcat -s PagBank

# Logs da aplicação
adb logcat -s DiangoPos

# Logs de rede
adb logcat -s OkHttp
```

## 📊 Monitoramento

### Métricas Importantes
- **Taxa de Aprovação**: % de transações aprovadas
- **Tempo de Resposta**: Latência das APIs
- **Erros de Rede**: Falhas de conectividade
- **Crash Rate**: Estabilidade da aplicação

### Firebase Crashlytics (Opcional)
```kotlin
// build.gradle (app)
implementation 'com.google.firebase:firebase-crashlytics-ktx:18.6.1'

// Uso
FirebaseCrashlytics.getInstance().log("Transaction processed")
```

## 🤝 Contribuição

### Como Contribuir

1. **Fork** o repositório
2. **Crie** uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. **Abra** um Pull Request

### Padrões de Código

#### Kotlin Style Guide
- Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Usar **ktlint** para formatação automática
- **Máximo 120 caracteres** por linha

#### Git Commit Messages
```
feat: adiciona processamento PIX
fix: corrige erro de validação de cartão
docs: atualiza README com novas instruções
test: adiciona testes para PaymentViewModel
```

#### Pull Request Template
```markdown
## Descrição
Breve descrição das mudanças

## Tipo de Mudança
- [ ] Bug fix
- [ ] Nova funcionalidade
- [ ] Breaking change
- [ ] Documentação

## Testes
- [ ] Testes unitários passando
- [ ] Testes instrumentados passando
- [ ] Testado manualmente

## Checklist
- [ ] Código revisado
- [ ] Documentação atualizada
- [ ] Changelog atualizado
```

## 📝 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
MIT License

Copyright (c) 2025 dianGO Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```

## 📞 Suporte

### Contatos
- **Email**: suporte@diango.com.br
- **Website**: https://diango.com.br
- **Documentação**: https://docs.diango.com.br/pos

### Links Úteis
- [Documentação PagBank](https://dev.pagseguro.uol.com.br/)
- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design Guidelines](https://material.io/design)

---

**Desenvolvido com ❤️ pela equipe dianGO**