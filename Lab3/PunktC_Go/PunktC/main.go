package main

import (
	"fmt"
	"math/rand"
	"time"
)

type Tobacco struct {
	available bool
}

type Paper struct {
	available bool
}

type Matches struct {
	available bool
}

func broker(tobChan chan Tobacco, papChan chan Paper, matChan chan Matches, semaphore chan int){
	i := 0
	for {
		if i>0{
			<-semaphore
		}
		fmt.Println("Broker took out something")

		tobacco := Tobacco{false}
		paper := Paper{false}
		matches := Matches{false}

		rand.Seed(time.Now().UnixNano())
		random := rand.Intn(3)

		if random == 0 {
			tobacco.available = true
			paper.available = true
		} else if random == 1 {
			tobacco.available = true
			matches.available = true
		} else{
			paper.available = true
			matches.available = true
		}

		time.Sleep(time.Second)
		fmt.Println("Broker put on table: tobacco:", tobacco.available, "| paper:", paper.available, "| matches:", matches.available)

		tobChan <- tobacco
		papChan <- paper
		matChan <- matches

		i++
	}
}

func smokerWithTobacco(tobChan chan Tobacco, semaphore chan int){
	ownTobacco := Tobacco{true}

	for {
		tobacco := <-tobChan

		if !tobacco.available && ownTobacco.available{
			fmt.Println("Smoker with Tobacco started smoking a cigarette!")
			time.Sleep(time.Second)
			fmt.Println("Smoker with Tobacco finished...")
			semaphore <- 1
		}
	}
}

func smokerWithPaper(papChan chan Paper, semaphore chan int){
	ownPaper := Paper{true}

	for {
		paper := <-papChan

		if !paper.available && ownPaper.available{
			fmt.Println("Smoker with Paper started smoking a cigarette!")
			time.Sleep(time.Second)
			fmt.Println("Smoker with Paper finished...")
			semaphore <- 1
		}
	}
}

func smokerWithMatches(matChan chan Matches, semaphore chan int){
	ownMatches := Matches{true}

	for {
		matches := <-matChan

		if !matches.available && ownMatches.available{
			fmt.Println("Smoker with Matches started smoking a cigarette!")
			time.Sleep(time.Second)
			fmt.Println("Smoker with Matches finished...")
			semaphore <- 1
		}
	}
}

func main()  {
	tobChan := make(chan Tobacco)
	papChan := make(chan Paper)
	matChan := make(chan Matches)
	semaphore := make(chan int)

	go smokerWithTobacco(tobChan, semaphore)
	go smokerWithPaper(papChan, semaphore)
	go smokerWithMatches(matChan, semaphore)
	broker(tobChan, papChan, matChan, semaphore)
}