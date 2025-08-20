# Create simulator object
set ns [new Simulator]

# Open trace and NAM files
set nr [open thro.tr w]
$ns trace-all $nr

set nf [open thro.nam w]
$ns namtrace-all $nf

# Define finish procedure
proc finish {} {
    global ns nr nf
    $ns flush-trace
    close $nf
    close $nr
    exec nam thro.nam &
    exit 0
}

# Create 12 nodes
for {set i 0} {$i < 12} {incr i} {
    set n($i) [$ns node]
}

# Create linear links between nodes 0 to 8
for {set i 0} {$i < 8} {incr i} {
    $ns duplex-link $n($i) $n([expr $i+1]) 1Mb 10ms DropTail
}

# Additional links forming alternate paths
$ns duplex-link $n(0) $n(8) 1Mb 10ms DropTail
$ns duplex-link $n(1) $n(10) 1Mb 10ms DropTail
$ns duplex-link $n(0) $n(9) 1Mb 10ms DropTail
$ns duplex-link $n(9) $n(11) 1Mb 10ms DropTail
$ns duplex-link $n(10) $n(11) 1Mb 10ms DropTail
$ns duplex-link $n(11) $n(5) 1Mb 10ms DropTail

# Create UDP agent and CBR traffic source at node 0
set udp0 [new Agent/UDP]
$ns attach-agent $n(0) $udp0

set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set interval_ 0.005
$cbr0 attach-agent $udp0

# Create UDP agent and CBR traffic source at node 1
set udp1 [new Agent/UDP]
$ns attach-agent $n(1) $udp1

set cbr1 [new Application/Traffic/CBR]
$cbr1 set packetSize_ 500
$cbr1 set interval_ 0.005
$cbr1 attach-agent $udp1

# Create a single Null agent (traffic sink) at node 5
set null0 [new Agent/Null]
$ns attach-agent $n(5) $null0

# Connect both traffic sources to the sink
$ns connect $udp0 $null0
$ns connect $udp1 $null0

# Set routing protocol to Link State
$ns rtproto LS

# Simulate link failures and recoveries
$ns rtmodel-at 10.0 down $n(11) $n(5)
$ns rtmodel-at 15.0 down $n(7) $n(6)
$ns rtmodel-at 20.0 up $n(7) $n(6)
$ns rtmodel-at 30.0 up $n(11) $n(5)

# Set flow IDs and colors for NAM
$udp0 set fid_ 1
$udp1 set fid_ 2
$ns color 1 Red
$ns color 2 Green

# Schedule traffic start/stop
$ns at 1.0 "$cbr0 start"
$ns at 2.0 "$cbr1 start"

# Optional: Label key nodes
$ns at 0.0 "$n(0) label Source1"
$ns at 0.0 "$n(1) label Source2"
$ns at 0.0 "$n(5) label Sink"

# Finish simulation
$ns at 45.0 "finish"

# Run the simulation
$ns run