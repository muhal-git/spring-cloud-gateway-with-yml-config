Şimdi adım adım SCG ile gateway nasıl yapılır inceleyelim.

Öncelikle https://start.spring.io/ adresinden ya da IDE eklentimiz ile bir Maven projesi oluşturmaya başlıyoruz.

Dependency ekleme kısmında **spring-cloud-starter-gateway** eklenmesi yeterli, *reactive gateway* yazdığınızda çıkanı eklerseniz **spring-cloud-starter-gateway** eklenecektir.

Ardından proje oluşturma işlemini tamamlayıp, projeyi açabilirsiniz.

BU SCG projesi ile bazı işlemler için hiç kod yazmadan application.yml dosyanızda yapacağınız yapılandırmalarla bir gateway oluşturabilirsiniz.

application.properties olarak adlandırılan dosya uzantısını yml olarak güncelleyin, çünkü dokümantasyonlarda konfigürasyonlar hep yml üzerinden yapılmış ve yml ile yapılabilien bazı düzenlemeler properties dosyası ile mümkün olmayabiliyor ya da zor oluyor.

yml dosyalarında indentation space ile yapiliyor tab ile değil, buna dikkat edin. Aksi takdirde hata alabilirsiniz. Ve indentation yaparken 2 space karakteri kullanılması öneriliyor.

## SCG İLE YÖNLENDİRME

application.yml dosyanızı açın ve aşağıdaki konfigürasyonu yapıştırın;

```yaml
server:
  port: 4141
logging:
  level:
    '[org.springframework]': TRACE
spring:
  cloud:
    gateway:
      mvc:
        routes:
        - id: redirectto_route
          uri: https://onlineislemler.kamusm.gov.tr # burasi kullanilmiyor fakat, kaldirildiginda da hata veriyor
          predicates:
          - Path=/kamusm
          filters:
          - RedirectTo=302, https://onlineislemler.kamusm.gov.tr
```

Yukaridaki konfigürasyonla /kamusm ye gelen istekleri "https://onlineislemler.kamusm.gov.tr" ye yönlendirecek. Query parametreler de aynı şekilde aktarılıyor. Örneğin /kamusm?key=value&key2=value2 şeklinde gelseydi https://onlineislemler.kamusm.gov.tr?key=value&key2=value2 şeklinde bir yönlendirme yapıyor.

Gateway sunucusunu ayağa kaldırarak denerseniz loglarda yönlendirme ile ilgili kısımları görebilirsiniz.

Fakat burada /kamusm/api şeklinde gelirse gateway sunucusu 404 hatası döner, onun yönlendirmesini istiyorsak Path=/kamusm, /kamusm/api şeklinde belirtebiliyoruz ya da farklı bir adrese yönlendirme istiyorsak başka bir routing yazabiliriz.

### Path Pattern Meanings:

1. `Path=/kamusm/**`
   - Matches zero or more directories/segments
   - Examples:
     - ✅ /kamusm
     - ✅ /kamusm/123
     - ✅ /kamusm/abc/def
     - ✅ /kamusm/a/b/c/d

2. `Path=/kamusm/*`
   - Matches exactly one directory/segment
   - Examples:
     - ✅ /kamusm/123
     - ✅ /kamusm/abc
     - ❌ /kamusm/abc/def
     - ❌ /kamusm

3. `Path=/kamusm/***`
   - This is not a valid pattern in Spring Cloud Gateway
   - Only `*` and `**` are supported

### Example Configuration:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: example_route
        uri: https://example.com
        predicates:
        - Path=/kamusm/**  # Use this for matching all nested paths
```

Burada kullanılan uri hakkında açıklama:

The `uri` property is actually required in Spring Cloud Gateway route configuration - it's a mandatory field even if you're using RedirectTo filter. Here's why:

1. **Architectural Requirement**: 
   - SCG's core routing mechanism requires a destination URI
   - The route definition won't be valid without it

2. **Best Practice Solution**:
   - Use a dummy but valid URI when using RedirectTo
   - Or use the actual target URI for better code readability

Using the actual target URI as the `uri` property makes the code more maintainable and self-documenting, even though it's not functionally used by the RedirectTo filter.

## SCG İLE REQUEST HEADER EKLEME

```yaml
spring:
  cloud:
    gateway:
      mvc:
        routes:
        - id: add_request_header_route
          uri: https://example.org
          filters:
          - AddRequestHeader=X-Request-red, blue
```

