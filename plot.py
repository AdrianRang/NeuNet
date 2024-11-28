import matplotlib.pyplot as plt
import numpy as np

file = open("out/out.txt")

generations = []
current_generation = []

for line in file:
    if line.strip().__contains__("#"): break
    if line.strip() == "-- GEN --":
        if current_generation:
            generations.append(np.array(current_generation))
            current_generation = []
    else:
        current_generation.append(float(line.strip()))

if current_generation:  # Add the last generation if file doesn't end with an empty line
    generations.append(np.array(current_generation))

file.close()

# for generation in generations:
#     plt.plot(generation)

bestPlayer = []

for generation in generations:
    bestPlayer.append(generation[0])
    

plt.plot(bestPlayer)
plt.xlabel("Generation")
plt.ylabel("Score")
plt.show()