# Create a simulator object
set ns [new Simulator]

# Open the NAM trace file
set nf [open out.nam w]
$ns namtrace-all $nf

# Define a 'finish' procedure
proc finish {} {
    global ns nf
    $ns flush-trace
    close $nf
    exec nam out.nam &
    exit 0
}

# Create eight nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]
set n6 [$ns node]
set n7 [$ns node]

# Create links between the nodes
$ns duplex-link $n0 $n3 1Mb 10ms RED
$ns duplex-link $n1 $n3 1Mb 10ms RED
$ns duplex-link $n2 $n3 1Mb 10ms RED
$ns duplex-link $n3 $n4 1Mb 10ms RED
$ns duplex-link $n4 $n5 1Mb 10ms RED
$ns duplex-link $n4 $n6 1Mb 10ms RED
$ns duplex-link $n4 $n7 1Mb 10ms RED

# Set link orientations for NAM visualization
$ns duplex-link-op $n0 $n3 orient right-up
$ns duplex-link-op $n1 $n3 orient right
$ns duplex-link-op $n2 $n3 orient right-down
$ns duplex-link-op $n3 $n4 orient middle
$ns duplex-link-op $n4 $n5 orient right-up
$ns duplex-link-op $n4 $n6 orient left
$ns duplex-link-op $n4 $n7 orient right-down

# Traffic flow 1: n2 → n5
set udp0 [new Agent/UDP]
$ns attach-agent $n2 $udp0

set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set interval_ 0.005
$cbr0 attach-agent $udp0

set null0 [new Agent/Null]
$ns attach-agent $n5 $null0
$ns connect $udp0 $null0

# Traffic flow 2: n1 → n6
set udp1 [new Agent/UDP]
$ns attach-agent $n1 $udp1

set cbr1 [new Application/Traffic/CBR]
$cbr1 set packetSize_ 500
$cbr1 set interval_ 0.005
$cbr1 attach-agent $udp1

set null1 [new Agent/Null]
$ns attach-agent $n6 $null1
$ns connect $udp1 $null1

# Traffic flow 3: n0 → n7
set udp2 [new Agent/UDP]
$ns attach-agent $n0 $udp2

set cbr2 [new Application/Traffic/CBR]
$cbr2 set packetSize_ 500
$cbr2 set interval_ 0.005
$cbr2 attach-agent $udp2

set null2 [new Agent/Null]
$ns attach-agent $n7 $null2
$ns connect $udp2 $null2

# Set flow IDs
$udp0 set fid_ 1
$udp1 set fid_ 2
$udp2 set fid_ 3

# Define colors for flows
$ns color 1 Red
$ns color 2 Green
$ns color 3 Blue

# Schedule traffic events
$ns at 0.1 "$cbr0 start"
$ns at 0.2 "$cbr1 start"
$ns at 0.5 "$cbr2 start"
$ns at 4.0 "$cbr2 stop"
$ns at 4.2 "$cbr1 stop"
$ns at 4.5 "$cbr0 stop"

# Call finish procedure
$ns at 5.0 "finish"

# Run the simulation
$ns run