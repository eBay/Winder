<img src="https://github.com/eBay/Winder/blob/master/docs/Winder-small.png"/>

# Winder
Winder is a simple state machine based on Quartz Scheduler. 
It helps to write multiple steps tasks on Quartz Scheduler.  Winder derived from a state machine which is widly used in eBay Cloud.
eBay Platform As A Service(PaaS) uses it to deploy software to hundreds of thousands virtual machines. 
The state machine is also using for maintaining hundreds of thousands VMs and keeping them healthy.

# Why do you need Winder?

####Case 1:
Your job has multiple tasks that can be finished in couple seconds, so that you have to let something take care of it.
Winder can do it in background.

####Case 2:
You have bunch of items, each item needs to be processed in couple of steps.Some of the step can be guaranteed finishing in couple seconds.
Winder can help you define the steps and let them work automatically.

####Case 3:
You have some batch tasks, they should be executed every day. 
Winder can get this done easily.

# Production use cases

Production Use Cases: widely used in many distributed systems which eBay Cloud relies on.

**Software Deployment/PaaS**: As the key part of eBay Cloud, Platform As A Service(PaaS) is widely used in eBay. Developer uses it to roll out code. Winder takes the key role in PaaS.

**Virtual machine maintenance**: eBay has hundreds of thousands VMs. Keeping them healthy and available is very important. Winder makes this possible.

**Provisioning container or VM**: Provisioning requires lots of communications with other systems. It is also built on Winder. 


# Examples

<a href="https://github.com/eBay/Winder/blob/master/winder-examples/src/main/java/org/ebayopensource/winder/examples/deployment1/DeploymentJob.java">**Software Deployment**</a>


<img src="https://github.com/eBay/Winder/blob/master/docs/SoftwareDeployment.png"/>

# Contributing
<div style="text-align:right">
  <img src="https://github.com/eBay/Winder/blob/master/docs/ebaysf-open-x.png" width="150px"/>
</div>
Refer to [CONTRIBUTING.md](/CONTRIBUTING.md) for more details on how to contribute code, documentation etc

**Winder** is freely usable, licensed under the [LICENSE.md](/MIT license).