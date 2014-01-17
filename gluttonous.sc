s.options.memSize = 650000;
s.boot;
s.scope;

~int8toint32l = { |l|
	(l[0] & 0xff << 0) | (l[1] & 0xff << 8) | (l[2] & 0xff << 16) | (l[3] & 0xff << 24)
};
~int8toint32 = { |l0,l1,l2,l3|
	(l0 & 0xff << 0) | (l1 & 0xff << 8) | (l2 & 0xff << 16) | (l3 & 0xff << 24)
};
~int8ArrayToInt32Array = { |l|
	var o = Int32Array.newClear(l.size / 4);
	forBy(0, l.size - 1, 4, { |i|
		o[i/4] = ~int8toint32.(l[i],l[i+1],l[i+2],l[i+3])
	});
	o	
};
~int8ArrayToInt32Array.(Int8Array[ -112, 3, 0 , 0, -112, 3, 0 , 0]);

~int8ArrayToInt32ArrayTest = {
	~arr = Int8Array[-112, 3, 0, 0];
	~int8to32l.(~arr).postln;
	~arr = Int8Array[-112, 3, 0, -1];
	~int8to32l.(~arr).postln;
	~arr = Int8Array[-112, 3, -1, 0];
	~int8to32.(~arr).postln;
	~arr = Int8Array[-112, 3, -1, 128];
	~int8to32l.(~arr).postln;
};

~arraysmaller = { |n,arr|
	var size = arr.size;
	Array.fill(n, {|i| arr[(size) * i/n]})
};
~arraysmaller.(10, Array.fill(100,{|i| i}));


SynthDef('myklang', {| freqs=#[30,40,80,120,160,200,300,400,500,600],
    amps=#[0,0,0,0,0,0,0,0,0,0],
    phases=#[0,0,0,0,0,0,0,0,0,0]|
	Out.ar(0,
		Mix.ar(
			SinOsc.ar( freq: freqs, phase: phases, mul: amps)))
}).add;

SynthDef('myklang10', {|freqs=#[0,0,0,0,0,0,0,0,0,0],
	amps=#[0,0,0,0,0,0,0,0,0,0],
	phases=#[0,0,0,0,0,0,0,0,0,0]|
	Out.ar(0,
		Mix.ar(
			SinOsc.ar( freq: freqs, phase: phases, mul: amps)))
}).add;
SynthDef('myklang100', {|freqs=#[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	amps=#[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	phases=#[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	out=0|
	Out.ar(out,
		Mix.ar(
			SinOsc.ar( freq: freqs, phase: phases, mul: amps)))
}).add;


~n = 100;
~afreqs = Array.fill(~n, {|i| 20 + (i*15.0)});
~bfreqs = Array.fill(~n, {|i| 20 + (i*2.5)});

//~phases = Array.fill(~n, {|i| 1.0*i/~n});
~aphases = Array.fill(~n, {0});
~bphases = Array.fill(~n, {0.01});
~aamp = Array.fill(~n, {1/~n});
~bamp = Array.fill(~n, {0});
a = Synth('myklang100',[
	out: 0,
	freqs: ~afreqs,
	amps: ~aamp,
	phases: ~aphases]);
b = Synth('myklang100',[
	out: 1,
	freqs: ~bfreqs,
	amps: ~bamp,
	phases: ~bphases]);
~aold = Array.fill(~n, {0});
b.setn(\amps,Array.fill(~n, {|i| 0.01 }));

~sounds = "./wavs/*.*a*".pathMatch.collect({arg path; Buffer.read(s,path)});

~bal = {
	arg amp = 1.0;
	var y = amp.rand;
	[1.0 - y, y]
};
SynthDef(\splayer, {
	arg buf, out=0, rate=1.0, looping=0, amp=1.0, myvol=[0.5,0.5];
	var myrate, trigger, frames,y;
	Out.ar(0,
		myvol * [amp,amp] * PlayBuf.ar(1, buf, [rate,rate], 1, 0, looping, 2)
	);
}).add;

~splayer = {
	arg buf, rate=1.0, looping=0, amp=1.0;
	{
		var myrate, trigger, frames, myvol;
		myvol = ~bal.(); 
		amp * myvol * PlayBuf.ar(1, buf, rate, 1, 0, looping, 2); 
	}.play();
};


~scanned = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];


~freqscale = 1.0;
~defaultplayer = { |v,i| ('freq': v*~freqscale).play };
/*
~defaultplayer = { |v,i| 
	('freq': v * 4.0 * ~freqscale).play;
	('freq': v * 3.0 * ~freqscale).play;
	('freq': v * 2.0 * ~freqscale).play;
	('freq': v*~freqscale).play };
*/

~fileplayer = {
	arg x, i, rate=1.0; 
	if( i % 5 == 0, {
		Synth(\splayer,[buf: ~sounds[x % ~sounds.size()] , rate: 1.0, myvol: ~bal.(0.1)]); 
	});
};


~bpm = 100;
~bar = 10;
~scanner = {
	arg f=~defaultplayer;
	var i = 0,j=0, v;
	Routine {
		loop {
			//0.1.wait;
			(60.0/~bpm/~bar).wait;		
			j = i % ~scanned.size();
			v = ~scanned[i % ~scanned.size()];
			if( v > 0, {
				//v.postln;
				f.(v,i);
				//('freq': v).play;
			});
			i = i + 1;
		}
	}.play;
};
60.0/100.0/10;
~startscanner = {
~ss1 = ~scanner.();
~ss2 = ~scanner.(~fileplayer);
};
//~scanned[23] = 66;

~klanglistener = {|msg|
	var out;
    //"My Klang 100 Listener".postln; 
	// part a
	out = ~int8ArrayToInt32Array.(msg[1])/1024.0;
	~aold = (~aold * 0.9) + (0.1 * out);
	~afreqs = ~aold * 1200;
	~bamp = (~bamp * 0.9) + (0.1 * (out/~n));
	a.setn(\freqs, ~afreqs,
		\amps, Array.fill(~n,{0.01}));
	b.setn(
		\out, 1,
		\freqs, ~bfreqs,
		\amps, ~bamp);
};

~scannerlistener = {|msg|
	var out;
    //"My Scanner Listener".postln; 
	out = ~int8ArrayToInt32Array.(msg[1]);
    ~scanned = out;
};



~listener = ~scannerlistener;
~listener = ~klanglistener;


~lwrap = {|msg| ~listener.(msg) };
~lboth = {|msg| ~klanglistener.(msg); ~scannerlistener.(msg) };


//OSCFunc.newMatching(~lwrap, '/samples');
OSCFunc.newMatching(~lboth, '/samples');
 

