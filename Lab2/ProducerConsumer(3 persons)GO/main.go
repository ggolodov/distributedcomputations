package main

import "fmt"

func ivanov_work(data_chan chan int)  {
	for i:=0; i<20; i++{
		data_chan<-1
		fmt.Println("Ivanov took a product from car and gave it to Petrov")
	}
}

func petrov_work(data_chan1, data_chan2 chan int)  {
	for i:=0; i<20; i++{
		<-data_chan1
		fmt.Println("Petrov took a product from Ivanov")
		data_chan2<-1
		fmt.Println("Petrov gave a product to Pilipets")
	}
}

func pilipets_work(data_chan chan int)  {
	for i:=0; i<20; i++{
		<-data_chan
		fmt.Println("Pilipets took a product from Petrov")
	}
}
func main() {
	var chan1 chan int = make(chan int)
	var chan2 chan int = make(chan int)
	go ivanov_work(chan1)
	go petrov_work(chan1,chan2)
	go pilipets_work(chan2)

	fmt.Scanln()
}
