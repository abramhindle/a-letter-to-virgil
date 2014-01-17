//s = Server(\myServer, NetAddr("127.0.0.1", 44556)); 
s.options.memSize = 650000;
s.boot;
s.scope;


~bal = {
	var y = 1.0.rand;
	[1.0 - y, y]
};
SynthDef(\splayer, {
	arg buf, out=0, rate=1.0, looping=0, amp=1.0, myvol=[0.5,0.5];
	var myrate, trigger, frames,y;
	Out.ar(0,
		myvol * [amp,amp] * PlayBuf.ar(1, buf, [rate,rate], 1, 0, looping, 2)
	);
}).add;

~beats = "./subbeats/*wav".pathMatch.collect({arg path; Buffer.read(s,path)});

~splayer = {
	arg buf, rate=1.0, looping=0, amp=1.0;
	{
		var myrate, trigger, frames, myvol;
		myvol = ~bal.(); 
		amp * myvol * PlayBuf.ar(1, buf, rate, 1, 0, looping, 2); 
	}.play();
};

~since = {
	arg since=10000.0;
	var amp = log(1.0+(abs(1000.0.min(since))))/log(1.0+10000.0);
	amp
};



~loopall = {
	arg vals, f, waittime=0.1;
	Routine {
		waittime.rand.wait;
		loop {
			f.(vals.choose).waitForFree;
		}
	}.play;
};

~looporder = {
	arg vals, f, waittime=0.1;
	Routine {
		waittime.rand.wait;
		loop {
			vals.do {|x| f.(x).waitForFree };
			//f.(vals.choose).waitForFree;
		}
	}.play;
};


~loopall.( ~beats, {|x| Synth(\splayer,[buf: x, rate: 0.99+0.1.rand, myvol: [0.5,0.5]]); });
~looporder.( ~beats, {|x| Synth(\splayer,[buf: x, rate: 1.0, myvol: ~bal.()]); });
