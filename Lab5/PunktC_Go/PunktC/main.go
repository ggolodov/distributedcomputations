package main

import (
	"fmt"
	"math/rand"
	"time"
)

var n int
var array1 []int
var array2 []int
var array3 []int

func sumChanger1(sumChan chan int, barrier1 chan bool, array1 []int){
	var sum int
	j := 0
	for  {
		if j>0{
			random := rand.Intn(len(array1))
			isBigger := <-barrier1

			if isBigger {
				array1[random] -= 1
			} else {
				array1[random] += 1
			}
		}

		sum = 0
		for i:=0;i<len(array1);i++  {
			sum += array1[i]
		}
		sumChan <- sum

		j++
	}
}

func checkArrays(sumChan1 chan int, sumChan2 chan int, sumChan3 chan int,
				barrier1 chan bool, barrier2 chan bool, barrier3 chan bool){
	for true {
		sum1 := <-sumChan1
		fmt.Println("Sum of array1 =", sum1)
		sum2 := <-sumChan2
		fmt.Println("Sum of array2 =", sum2)
		sum3 := <-sumChan3
		fmt.Println("Sum of array3 =", sum3)

		if sum1 == sum2 && sum1 == sum3 {
			fmt.Println("          |")
			fmt.Println("          V")
			fmt.Println("Sums of arrays are equal")
			break
		}else{
			fmt.Println("-------------------")
			if sum1 >= sum2{
				if sum1 >= sum3{
					barrier1 <- true
					barrier2 <- true
					barrier3 <- false
				}else{
					barrier1 <- false
					barrier2 <- false
					barrier3 <- true
				}
			} else {
				if sum2 >= sum3{
					barrier1 <- false
					barrier2 <- true
					barrier3 <- false
				} else {
					barrier1 <- false
					barrier2 <- true
					barrier3 <- true
				}
			}
		}
	}
}

func init(){
	rand.Seed(time.Now().UnixNano())

	n = 5
	array1 = make([]int, n)
	array2 = make([]int, n)
	array3 = make([]int, n)
	for i:=0;i<n;i++  {
		array1[i] = i
		array2[i] = i + 4
		array3[i] = i + 2
	}
}

func main() {
	sumChan1 := make(chan int)
	sumChan2 := make(chan int)
	sumChan3 := make(chan int)
	barrier1 := make(chan bool)
	barrier2 := make(chan bool)
	barrier3 := make(chan bool)

	go sumChanger1(sumChan1, barrier1, array1)
	go sumChanger1(sumChan2, barrier2, array2)
	go sumChanger1(sumChan3, barrier3, array3)
	checkArrays(sumChan1, sumChan2, sumChan3, barrier1, barrier2, barrier3)
}
