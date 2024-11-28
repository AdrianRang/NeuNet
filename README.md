# NeuNet
A Neural Network made from scratch in java

## How it works
### The network
#### Neurons
There are three types of neurons:
- Input Neurons
- Hidden Neurons
- Output Neurons

Input Neurons will get a value from the game and output it.  

---

Hidden Neurons have an input and an output, they will take the input and apply a bias (offset) and an activation function (RELU (max(0, x)), SIGMOID, etc...) and output that value  

---

Output Neurons just ake the input and will display it for the game to use  

#### Connectors
A connector can go from any neuron to any other neuron as long as the from neuron layer precedes to neuron layer.
It has a weight (a multiplier).

---

And this simple system is able to do very complex tasks like number recognition.  
Although if you crate a network with random hidden neurons and connectors it will not do what you want it to do, for that you need to train it.

### The training
There are many ways to train an AI or Neural Network, like Reinforcement training and Dimensionality Reduction. But in this project we train it using an evolutonary approach.  
The Evolutonary approach works just like IRL Evolution, you have a bunch of organisms that live and the ones that did better at living get to reproduce.  
Here we have a variable amount of agents that are evaluated using an [Evaluation Function](#evaluation-function) to choose who gets 'children' and how many. Evolution doesn't work if you don't have mutations, so we also have to add those in. I do recommend keeping at least 1 the same so you don't loose progress.

Trainig takes a long time, it normally follows a log(n) speed, starting by getting good fast and then slowing down a lot, you can look at examples of this in the out directory (you can plot them by running [plot.py](./plot.py))

#### Evaluation Function
The Evaluation funtion is made by you to test how well the Neural Network is doing this project uses [this](https://www.desmos.com/calculator/dc1lqebg9n) one

Remember that training takes a lot of fine tuning, how much it randomizes, how to start, and the Evaluation Function.

---

## The objective
The Neural network's objective will be to balance a single pendulum upwards

(Yes I got the idea from a YouTuube video that I lost and can't find again)

## The Network
The network has 4 Inputs and one output

Inputs:
- The cart's x position
- The cart's speed
- The Pendulum's angle
- The Penddulum's speed

Outputs:
- The carts speed
> [!NOTE]
> Might change to acceleration

## The Training
It trains, can generate great outputs, takes a while, in the out directory there's the score of all generations sorted from most to least, Some of the outputs were evaluated with different evaluation functions/frame counts aand that means the score is not relative from one to the other

## Results
This is the first ever time it worked
![1st](https://cloud-cfl2jh8pm-hack-club-bot.vercel.app/0worked__1.png)
(Note that the renderer broke somewhere along the way of coppying networks)

This is the one called network.json
![net.json](https://cloud-a1gfnk0uh-hack-club-bot.vercel.app/0net.png)

look at a video 👀 [here](https://cloud-2j83mmbzx-hack-club-bot.vercel.app/0demoneunet.mp4)

## How to run it
Download the github repository
````
git clone https://github.com/AdrianRang/NeuNet
````

run [Main.java](./src/Main.java) to train a new one
or run [RunNetwork.java](./src/RunNetwork.java) to run a pre-existing one. 
RunNetwork will run the one saved in the JSON named `network.java` you can change this by changing the string named path on line 32 to the path of your network

---

When you run Main.java it will automattically create the out.txt and network.json, if you have others be sure they are not named that or they will be replaced.  

You can change the values used to train the network go to [Constants.java](./src/Constants.java) and change them there.