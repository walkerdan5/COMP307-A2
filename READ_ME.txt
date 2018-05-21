To run Part one (bpnn):
**there is no runnable jar for part one, you just have to be in the right directory within the terminal, then pass in the arguments***
Extract "Part1bpnn.zip"
Through terminal: cd Part1bpnn
			% ./nntrain iris.net iris.pat //training epochs will display (around 1500 epochs)
				% ./nntest iris.net iris.pat weights.dat //testing output will display, MSE etc
see "Part1_sampleOutput.txt" for reference


====

To run Part two:
Use the runnable jar named "A2_Part2.jar"
		through the terminal type the command:
			% java -jar A2_Part2.jar regression.txt

(Regression.txt is the argument the jar requires to run)
I have used the JGAP library downloaded through IntelliJ Maven Repo
I have tested this on the ecs machines and it would only run after I added this library to the IntelliJ project. So the tutor may have to open my A2_Part2 project in IntelliJ and install the library before running the jar.

see"A2_Part2_Sample_Output.txt" for reference

Part 3 not attempted
