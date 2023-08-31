# Directed Weighted Graph - OOP_Ex2:
Welcome to the "Directed Weighted Graph" project! In this repository, we have developed a code that handles directed weighted graphs, allowing us to read graphs from JSON files and perform various operations on them.
https://docs.google.com/document/d/17h5VGIHtqWHrzgoRjH05_PjHgCn8-EDcecrkR9sVChQ/edit

## Screenshots:
![image](https://github.com/tzachaker/Ex2_OOP/assets/76492492/1f70fb97-e3e6-4065-9227-cf32dfce557c)
![image](https://github.com/tzachaker/Ex2_OOP/assets/76492492/6b1e2c08-6b40-4caf-826d-33d35a828a9d)
![image](https://github.com/tzachaker/Ex2_OOP/assets/76492492/92af3436-1258-4f08-8922-7c56aff23deb)
![image](https://github.com/tzachaker/Ex2_OOP/assets/76492492/120548c6-335c-4333-8614-2a9d1be89310)






## Project Overview
In this project, our main focus was on working with directed weighted graphs. The project encompasses the following key features:

- Reading graphs from JSON files.
- Verifying if the graph is strongly connected.
- Determining the shortest path between two nodes.
- Calculating the shortest path from a source node to a destination node, along with the intermediate nodes.
- Saving the graph after applying various algorithms to enhance efficiency.
- Checking for the existence of the Traveling Salesman Problem (TSP) with Hamiltonian Circle.
- Calculating the center node of the graph, which is the node closest to all other nodes.
- Displaying the graph in a graphical user interface (GUI) window, with additional menu options.

## Algorithm and Implementation
We leveraged the Dijkstra Algorithm to determine graph connectivity and to find the shortest path between nodes. The core functions of our algorithm are as follows:

- Reading JSON files to load graphs.
- Verifying the graph's strong connectivity.
- Computing the shortest path and intermediate nodes using Dijkstra's Algorithm.
- Saving graphs after applying optimization algorithms.

## Getting Started
To run the code, you can follow these steps:

1. Download the JAR file "Ex2/Ex2.jar" from this repository.
2. Alongside the JAR file, there are three JSON files. You can use these as examples or load your JSON files following the same format.
3. In your command line, run the code using the command: java -jar Ex2.jar G2.json (Replace "G2.json" with your chosen JSON file).
3. The program will execute, displaying the graph and its properties.
   
## GUI Interface
We have incorporated a GUI interface that enables you to visualize and manipulate graphs. Here are some GUI features:

- Loading and saving graphs via the "SAVE\LOAD" menu.
- Editing graph nodes and edges using the "GRAPH" button.
- Accessing our algorithms through the "ALGORITHM" button.

## Note
Please note that while our project includes the "shortestPathDist" function to calculate the shortest path between nodes, this function is still under development and might not work as expected. Due to this ongoing development, we haven't yet provided a detailed report or results for running time on larger graphs.

## Feel free to explore our project, utilize the features, and contribute to further enhancing its functionality!

Developed by Tzach and Yohanan - The Development Team of the Project.
