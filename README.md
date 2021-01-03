# SpringBoot InMemory DB using Hazelcast

* application memory 를 이용한 in memory db 활용 샘플
* hazelcast의 clustering 을 이용해 여러 instance 에서 데이터 동기화   

## dependency

```xml
    <dependency>
        <groupId>com.hazelcast</groupId>
        <artifactId>hazelcast-all</artifactId>
        <version>4.1</version>
    </dependency>
```

## configuration

* `src/main/resources/hazelcast.yaml` 파일

```yaml
hazelcast:
  cluster-name: hazelcast-cluster
  hazelcast:
    network:
      join:
        multicast:
          enabled: true
  map:
    default:
      time-to-live-seconds: 0
      max-idle-seconds: 10
      eviction:
        eviction-policy: LRU
        max-size-policy: PER_NODE
        size: 5000
```
1. `cluster-name` 으로 application group 설정(클러스터용 내부 통신 `port` 로 `5701`을 사용하지 못하면 자동으로 `5702`,`5703` 을 사용함(auto-increment))
1. `multicast` 방식으로 member discovery
1. `map` eviction 처리
  1. `time-to-live-seconds` : TTL / 생성 후 최대 생존 시간(초)
  1. `max-idle-seconds` :  / idle 시간 내 참조 시 갱신
  1. `eviction`
     1. `eviction-policy`: LRU, LFU, NONE(default, ttl과 max-idle만 eviction 영향)
     1. `max-size-policy`: PER_NODE, PER_PARTITION....
     1. `size`: map 최대 사이즈 

> * map eviction 처리 시 정수 값은 0(infinite) ~ Integer.MAX_VALUE
> * `time-to-live-seconds` 와 `max-idle-seconds` 를 같이 사용 할 시, 둘 중 먼저 expire 조건 만족하는 것 사용 됨
> * [Eviction Algorithm](https://docs.hazelcast.org/docs/4.1.1/manual/html-single/index.html#eviction-algorithm)


> **_전체설정정보_:** [hazelcast-full-example.yaml](https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/main/resources/hazelcast-full-example.yaml)

> spring profiles 에 따른 환경설정 파일 분리는 안되는 것 같음. java config 으로 처리 하자


## Cluster discovery mechanism

* discovery mechanisms
  1. Auto detection : (기본-자세한 메카니즘 설명은 없음) `hazelcast-all` 이면, 실행 환경에 따라 자동 적용 / 적절한것이 없으면 `*Multicast*` 방식
  1. TCP : tcp 방식으로 cluster member 설정
  1. Multicast : 네트워크 broadcasting (UDP 사용!)
  1. AWS, Azure, GCP Cloud discovery
  1. Kubernetes
  1. Eureka
  1. Zookeeper

* 운영 환경에서는 위와 같이 multicast 를 이용한 auto-discovery 보다는 [TCP로 Member찾기](https://docs.hazelcast.org/docs/4.1.1/manual/html-single/index.html#discovering-members-by-tcp)
```yaml
hazelcast:
  network:
    join:
      tcp-ip:
        enabled: true
        member-list:
          - machine1
          - machine2
          - machine3:5799
          - 192.168.1.0-7
          - 192.168.1.21
```
 

## Running

* test run on `8080`
```shell
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080"
```

* test run on `8081`
```shell
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
```

* key,value get/set
```shell

```

* get book name by isbn
```shell
curl localhost:8080/books/isbn1

curl localhots:8081/books/isbn1
```

## 매뉴얼 InMemory DB 사용

* HazelCast Instance
```java
    private final HazelcastInstance hazelcastInstance;

    public CommandController(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
```

> HazelcastInstance 에서 아래 자료 구조를 가져와 사용 할 수 있음 (getXXX("name"))
> 
> Queue, Topic, Set, List, Map, ReplicatedMap, MultiMap, Ringbuffer, 


* key-value get/set
  * get cache map that named `cache-map`
```java
    hazelcastInstance.getMap("cache-map");
```
  * set key,value (key1:cache-value) in "cache-map" map 
```java
    hazelcastInstance.getMap("cache-map").put("key1", "cache-value");
```


## Reference

* [Hazelcast Reference Manual - v4.1.1](https://docs.hazelcast.org/docs/4.1.1/manual/html-single/index.html)
* [Getting Started with Hazelcast using Spring Boot](https://guides.hazelcast.org/hazelcast-embedded-springboot/) : #official #tutorial
* [Caching with Spring Boot and Hazelcast](https://guides.hazelcast.org/caching-springboot/): #official #caching #spring-boot 
* [baeldung java-hazelcast](https://www.baeldung.com/java-hazelcast) : #basic #java_configuration #management_center
* [Spring Boot: Distributed Caching using Hazelcast](https://sunitc.dev/2020/09/12/spring-boot-distributed-caching-using-hazelcast/) : #distributed_cache
* [Hazelcast and Spring Boot for Scalable Task Execution - A How-To Guide](https://hackernoon.com/hazelcast-and-spring-boot-for-scalable-task-execution-a-how-to-guide-tp1e3wir) : #spring_profiles #java_config