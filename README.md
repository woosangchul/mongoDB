## 프로젝트 구조도
![카르텔구조도](https://user-images.githubusercontent.com/40749537/188895970-d6ce8a60-30cf-48dc-a4c8-9fba0e00c434.png)


## 구현내용
1. 스프링배치를 통해 6시간 간격으로 클레이튼 블록체인 노드의 스테이킹 상태를 KARTEL 몽고DB에 업데이트
2. 보유한 코인과 가격정보 API 제공
3. 보유한 nft와 스테이킹한 nft정보 조회

## 패키지 구조

```
mongodb
    ├─batch
    │  ├─jobs
    │  ├─schedulers
    │  └─tasklets
    ├─connection
    ├─controller
    ├─dto
    ├─entity
    └─service
```

## 제공되는 API
**보유한 코인과 가격정보 조회**

> **GET** /user/tokenInfo/{walletAddress}
```
 [
 {"address":"0x8D5516d63213304647D2702f8027f0eEF1a2480b",
 "price":1,
 "oUSDC":"0x754288077d0ff82af7a5317c7cb8c444d421d103"}
 ]
 ```


**보유한 nft와 스테이킹한 nft정보 조회**

> **GET** /user/tokenInfo/{walletAddress}

```
[
{"nft_contract":"0xc6fc271db0ecc36aa43653041476e2095a817956"
"address":"0x8d5516d63213304647d2702f8027f0eef1a2480b",
"nft_id":"0x525"}
]
```






