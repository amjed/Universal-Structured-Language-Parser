%%==================================================================================================
%%      ORDERED SETS FUNCTIONS
%%          - by Umran Azziz
%%==================================================================================================

%%--------------------------------------------------------------------------------------------------
%% empty_set( Set ).
%%--------------------------------------------------------------------------------------------------

empty_set([]).

%%--------------------------------------------------------------------------------------------------
%% in_set( Item, Set ).
%%--------------------------------------------------------------------------------------------------

in_set( Item, [Item] ) :- !.
in_set( Item, [Head | Tail] ) :- 
	(Item=Head, !) ;
	(Head @< Item -> in_set(Item, Tail)).

%%--------------------------------------------------------------------------------------------------
%% not_in_set( Item, Set ).
%%--------------------------------------------------------------------------------------------------

not_in_set( Item, [] ) :- !.
not_in_set( Item, [Head | Tail] ) :-
	Item\=Head,
	(Head @< Item -> not_in_set(Item, Tail) ; !).

%%--------------------------------------------------------------------------------------------------
%% insert_to_set( Item, InputSet, OutputSet ).
%%--------------------------------------------------------------------------------------------------

intern_insert_to_set( Item, [], Fwd, Result ) :-
	append(Fwd,[Item],Result), !.
intern_insert_to_set( Item, [Head | Tail], Fwd, Result ) :- 
	(
		Item @=< Head ->
		(
			(Item=Head ->  ItemHead=[Head] ; ItemHead=[Item,Head]),
			append(Fwd,ItemHead,NewFwd),
			append(NewFwd,Tail,Result)
		) ; 
		(
			append(Fwd,[Head],NewFwd),
			intern_insert_to_set(Item,Tail,NewFwd,Result)
		)
	).

insert_to_set( Item, Set, Result ) :- intern_insert_to_set( Item, Set, [], Result ).

%%--------------------------------------------------------------------------------------------------
%% to_set( List, Set ).
%%--------------------------------------------------------------------------------------------------

to_set( [], [] ).
to_set( [Head], [Head] ) :- !.
to_set( [Head | Tail], Set ) :-
	to_set(Tail,InnerSet),
	(
		not_in_set(Head,InnerSet) -> insert_to_set(Head,InnerSet,Set) ; Set=InnerSet
	).

%%--------------------------------------------------------------------------------------------------
%% merge_sets( SetA, SetB, ResultSet ).
%%--------------------------------------------------------------------------------------------------

intern_merge_sets( [], [], Fwd, Fwd ).
intern_merge_sets( [], [Head | Tail], Fwd, ResultSet ) :-
	append(Fwd,[Head],NewFwd),
	intern_merge_sets([], Tail, NewFwd, ResultSet ).
intern_merge_sets( [Head | Tail], [], Fwd, ResultSet ) :-
	append(Fwd,[Head],NewFwd),
	intern_merge_sets([], Tail, NewFwd, ResultSet ).
intern_merge_sets( [HeadA | TailA], [HeadB | TailB], Fwd, ResultSet ) :-
	( HeadA=HeadB ->
		(
			append( Fwd, [HeadA], NewFwd ),
			intern_merge_sets( TailA, TailB, NewFwd, ResultSet ) 
		) ;
		( HeadA @< HeadB -> 
			(
				append( Fwd, [HeadA], NewFwd ),
				intern_merge_sets( TailA, [HeadB | TailB], NewFwd, ResultSet )
			) ;
			(
				append( Fwd, [HeadB], NewFwd ),
				intern_merge_sets( [HeadA | TailA], TailB, NewFwd, ResultSet )
			)
		)
		
	).

merge_sets( SetA, SetB, ResultSet ) :- intern_merge_sets( SetA, SetB, [], ResultSet ).

%%--------------------------------------------------------------------------------------------------
%%  iterate_set( Set, Item ).
%%--------------------------------------------------------------------------------------------------

iterate_set( [Head | Tail], Item ) :-
	Item=Head ;
	iterate_set( Tail, Item ).

%%--------------------------------------------------------------------------------------------------
%%      End of Ordered Sets Functions
%%--------------------------------------------------------------------------------------------------

