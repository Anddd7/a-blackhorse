| cardId | cardType | cardTitle | points | reporter | decompositionCost | startAt | endAt | developer | title | expectCost | actualCost | blockCost | blockType | blockInstruction | blockConsequence |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| STORY-10001 | STORY | Get the shopping cart info | 8 | Zhang3 | 200 | 2021-05-19 | 2021-05-27 | Li4 | render empty shopping cart | 60 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call bff api | 30 | 20 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call service to get dto | 30 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call feign client to get dto | 30 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call backend to get dto | 30 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call usecase | 60 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | render shopping cart | 30 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call bff api | 30 | 15 |  |  |  |  |
|  |  |  |  |  |  |  |  |  | call service | 30 | 30 | 100 | STUDY | learn how to retrieve id from jwt in header |  |
|  |  |  |  |  |  |  |  |  | call feign client | 30 | 20 | 200 | INTEGRATION_TESTING | test feign client with upstream services |  |