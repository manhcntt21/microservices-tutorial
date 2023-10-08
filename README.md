Project này triển khai hệ thống microservice sau:

![img.png](images/img.png)

Sau khi chạy docker-compose up -d

1. Dashboard cho keylock: http://localhost:8181/
2. Service Discovery dardboard: http://localhost:8761/
3. Distributed Tracing dashboard: http://localhost:9411/zipkin/
4. Health check của order-service: http://localhost:8081/actuator

# Outline project
1. Produce Servece
2. Order Service
3. Service Discovery
4. API gateway
5. Distributed tracing
6. Message Queue

## Cụ thể gồm các phần sau
### 1 Product service: 
Dùng cho việc truy vấn các sản phẩm mà hệ thống có hay không?
- Dùng mongodb cho việc get dữ liệu nhiều mà hầu như không có thao tác chỉnh sửa
- không có user/pass, nếu các bạn thích cấu hình security cho mongodb có thể tham khảo [spring boot with mongodb](https://github.com/manhcntt21/springboot-mongodb), mình đã làm
- Dùng MongoCompass GUI để thao thác với mongodb
![img_1.png](images/img_1.png)

### 2 Order service:
Thực hiện order sản phẩm, frontend sẽ gọi nó
- database: postgresql, mình chuyển từ mysql sang postgresql
- user/pass: root/root
- database: order-service

#### 2.1 Communication between services
- Mình dùng webflux vì nó hỗ trợ sync và async, các bạn có thể dùng restemplate(theo mình biết nó chỉ hỗ trợ sync call)

### 3 Service Discover using Netflix Eureka của Spring Cloud
![img_2.png](images/img_2.png)

Về cạch cấu hình các bạn xem ở application.properties
Thằng cha sẽ có
```properties
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```
Thằng con:
```properties
eureka.client.serviceUrl.defaultZone=http://eureka:password@localhost:8761/eureka
eureka.client.fetch-registry=true
eureka.client.register-with-eureka=true
eureka.instance.prefer-ip-address=true
```

### 4 API GATEWAY
Mục đích là để route request từ bên ngoài vào hệ thống, mình dùng Spring Cloud Gateway của Sring Cloud, nó cũng có hỗ trợ Circui Breaker, Load Balancer,...

Ví dụ:
![img_3.png](images/img_3.png)

#### 4.1 Authen Service tại API GATEWAY
Mình dùng Keycloak, vì mình đã cấu hình sẵn realm ở thư mục [realms](./realms), các bạn có thể truy cập vào dashboard của Keycloak để setup rồi export ra file để dùng cho chạy lần sau không phải tạo lại nữa
#### 4.2 Circuit Breaker
Đây là một pattern để ngắt một quá trình khi hệ thống gặp sự cố, khi số lượng requests đột ngột tăng cao thì hệ thông không bị chết, ở đây mình dùng **Resilient4J**

![img_4.png](images/img_4.png)

Nó gồm 3 trạng thái normal và 2 trạng thái đặc biệt, ở project này mình chỉ cần dùng 3 trạng thái normal: closed, open, half open
- Closed: khi hệ thống hoạt động bình thường thì circuit state là closed
- Open: khi hệ thống có vấn đề, ví dụ khi order service gọi inventory service, đợi mãi mà không thấy phản hồi thì trạng thái của nó là open
- Half open: trạng thái trung gian để gọi inventory sau một khoảng duration time, nếu ok thì chuyển về closed, nếu không ok thì lại chuyển về open
Chi tiết về cấu hình, các bạn xem ở application.properties của **order service**

### 5 Distributed tracing
Để quản lý hành trình của request, biết nó có lỗi ở đâu mà thời gian ở đoạn nào chiểm nhiều
Ví dụ:

![img_5.png](images/img_5.png)

### 6 Message Queue
Mình dùng kafka, ngoài ra các bạn có thể dùng RabbitMQ, ở đây mình triển khai đơn giản nên chưa có nhiều sự khác biệt, mình cũng không có nhiều kinh nghiệm về cái này, chỉ đơn giản là log event, khi order thành công sẽ produce một message đến broker và **inventory service** sẽ consume nó, ở đây các bạn hoàn toàn có thể gửi email hoặc tin nhắn đến điện thoại,...
