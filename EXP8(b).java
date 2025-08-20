# Create a simulator object
set ns [new Simulator]

# Define different colors for data flows
$ns color 1 Blue
$ns color 2 Red

# Open the NAM trace file
set nf [open out.nam w]
$ns namtrace-all $nf

# Create four nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]

# Create links between the nodes
$ns duplex-link $n0 $n2 1Mb 10ms DropTail
$ns duplex-link $n1 $n2 1Mb 10ms DropTail
$ns duplex-link $n2 $n3 1Mb 10ms SFQ

# Specify layout of nodes for NAM visualization
$ns duplex-link-op $n0 $n2 orient right-down
$ns duplex-link-op $n1 $n2 orient right-up
$ns duplex-link-op $n2 $n3 orient right

# Monitor the queue for the link n2 → n3 vertically
$ns duplex-link-op $n2 $n3 queuePos 0.5

# Create a UDP agent and attach it to node n0
set udp0 [new Agent/UDP]
$udp0 set class_ 1
$ns attach-agent $n0 $udp0

# Create a CBR traffic source and attach it to udp0
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set interval_ 0.005
$cbr0 attach-agent $udp0

# Create a UDP agent and attach it to node n1
set udp1 [new Agent/UDP]
$udp1 set class_ 2
$ns attach-agent $n1 $udp1

# Create a CBR traffic source and attach it to udp1
set cbr1 [new Application/Traffic/CBR]
$cbr1 set packetSize_ 500
$cbr1 set interval_ 0.005
$cbr1 attach-agent $udp1

# Create a Null agent (traffic sink) and attach it to node n3
set null0 [new Agent/Null]
$ns attach-agent $n3 $null0

# Connect traffic sources with the traffic sink
$ns connect $udp0 $null0
$ns connect $udp1 $null0

# Define finish procedure
proc finish {} {
    global ns nf
    $ns flush-trace
    close $nf
    exec nam -a out.nam &
    exit 0
}

# Label nodes for NAM visualization
$ns at 0.0 "$n0 label Sender1"
$ns at 0.0 "$n1 label Sender2"
$ns at 0.0 "$n2 label Router"
$ns at 0.0 "$n3 label Receiver"

# Schedule events for the CBR agents
$ns at 0.5 "$cbr0 start"
$ns at 1.0 "$cbr1 start"
$ns at 4.0 "$cbr1 stop"
$ns at 4.5 "$cbr0 stop"

# Call finish procedure after simulation
$ns at 5.0 "finish"

# Run the simulation
$ns run