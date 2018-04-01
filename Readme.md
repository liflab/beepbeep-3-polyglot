Polyglot: multiple interpreters for BeepBeep 3
==============================================

This package contains a front-end to the BeepBeep 3 event stream query
engine. This front-end provides multiple interpreters reading expressions
in various specification languages, and converting them into the
equivalent chain of BeepBeep processors.

It currently provides interpreters for the following notations:

- Linear Temporal Logic (LTL)
- LOLA

In all cases, the interpreter extends the capabilities of the original
languages by adding other features supported by the BeepBeep event stream
engine, thereby generalizing each specification language.

Table of Contents                                                    {#toc}
-----------------

- [The LOLA interpreter](#lola)
- [About the author](#about)

The LOLA interpreter                                          {#lola}
--------------------

Suppose that `exp` is a string variable containing the following LOLA
specification:

    s1 = true
	s2 = t3
	s3 = t1 | ((t3) < (1))
	s4 = ((t3)^(2) + (7)) mod 15
	s5 = ite(s3; s4; (s4) + (1))
	s6 = ite(t1; (t3) < (s4); !(s3))
	s7 = t1[+1; false]
	s8 = t1[−1; true]
	s9 = s9[−1;0] + t3
	s10 = t2 | (t1 & s10[1; true])

Consider the following piece of code:

    LolaInterpreter lol_int = new LolaInterpreter();
    NamedGroupProcessor ngp = (NamedGroupProcessor) lol_int.parse(exp);
    Processor t1, t2, t3; // Define these processors ...
    Connector.connect(t1, 0, ngp, ngp.getInputIndex("t1"));
    Connector.connect(t2, 0, ngp, ngp.getInputIndex("t2"));
    Connector.connect(t3, 0, ngp, ngp.getInputIndex("t3"));
    Pullable p = ngp.getPullableOputput("s10");
    while (p.hasNext()) {
      ...
    }

This code does the following:

1. Create a new LOLA interpreter
2. Parse the LOLA specification from a string
3. Since the streams t1, t2 and t3 are considered as input
   streams in the specification, associate them to actual sources
   of events
4. Get a hold of the `Pullable` object corresponding to the
   output of s10
5. Iterate over events output by s10

That's it! As one can see, this interpreter is just an alternate way
of instantiating and piping BeepBeep processors, using the syntax of the
LOLA language.

### Features                                                     {#features}

Since the interpreter is simply a front-end to BeepBeep, many constructs
not present in the original LOLA have been added to the language. We list
a few of them here.

#### Window functions

The construct `win(fname, streamname, w)` can be used to denote the
application of function `fname` on a sliding window of width `w` from the
events of `streamname`. For example, the cumulative sum is computed by
replacing `fname` by `sum`. Virtually any function can go in place of
`fname`.

#### Mutator

One can turn an arbitrary stream `s` into a stream of the same constant
`k` by writing `convert s into k`. This corresponds to the `Mutator`
processor in BeepBeep.

About the author                                                   {#about}
----------------

The BeepBeep interpreters
were written by [Sylvain Hallé](http://leduotang.ca/sylvain) with
contributions from Raphaël Khoury, both
professors at [Université du Québec à
Chicoutimi](http://www.uqac.ca/), Canada.
