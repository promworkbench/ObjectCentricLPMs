
# ProM package for the discovery of OCLPMs

Master thesis by Marvin Porsil\
OCLMPs = object-centric local process models

## Initial thesis description

Discovery of Object-Centric Local Process Models (M.Sc.)\
Supervisor: Viki Peeva (peeva@pads.rwth-aachen.de)

Process discovery is one of the fundamental tasks in process mining, where one tries to discover a model that explains all traces in the event log from start to end. However, given that the event log contains highly unstructured behavior, process discovery algorithms struggle to model the behavior in a structured and well-understandable process model. There are different strategies to handle these situations, one of which is local process model discovery. In contrast to process discovery, local process model discovery aims to find a set of smaller models that describe what happens locally in the event log. Giving insights for event logs where traditional process discovery techniques failed was the primary purpose of local process models. However, with time, the importance and application of local process models grew and became multi-fold. Currently they are used for event abstraction, trace clustering, outcome prediction, etc.

Concurrently the idea of object-centric event logs emerged, where instead of connecting each event to a unique case, events refer to multiple objects. This led to the new object-centric event log standard (http://www.ocel-standard.org/) and consequently to object-centric Petri nets that model the process, together with the interaction between the different objects.

Although multiple approaches exist for discovering local process models from event logs, none can build local process models from object-centric event logs. The goal of this thesis is to create an algorithm that, given an object-centric event log, will discover and return object-centric local process models. In addition to the algorithm, a user friendly and fully functional UI is required.

## Concept

TODO

## How to use

### Setup

1. Clone this git repository
2. Import it as a java project with the name "**OCLPMs**"
3. Set the JRE System Library to jdk 8
4. Set the Java compiler compliance level to 1.8
5. If necessary, install ivy extension and let it resolve ivy dependencies
6. Run ProM using the *ProM with UITopia (OCLPMs).launch* file

### Usage of this package itself

TODO