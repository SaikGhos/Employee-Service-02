# Employee-Service-02

Used :  Spring Web flux(Reactive Spring REST API), Mongo DB, Kafka(producer), Hazelcast, @RequestParam

o	This application has all the functionalities that the 1st one(Employee-Service) have but with better executions. E.g.

- Implemented a Springboot REST endpoint - findEmpSkillSet - that fetches a list if employees from Cassandra matching the input(Java exp/Spring exp/Both)

- Used @RequestParam instead of @PathVariable--
	use case: Now we can find employee either by both java & spring experiences or by just one of them as requested during the API call.

- Added update employee API

