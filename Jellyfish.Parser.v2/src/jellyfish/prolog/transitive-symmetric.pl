%%--------------------------------------------------------------------------------------------------

inferred_{RELATIONSHIP_NAME}( [Head | Tail], EntityA, EntityB ) :-
	EntityA = Head, iterate_set( Tail, EntityB ) ;
	inferred_{RELATIONSHIP_NAME}( Tail, EntityA, EntityB ).

inferred_{RELATIONSHIP_NAME}( EntityA, EntityB ) :-
	asserted_{RELATIONSHIP_NAME}( EntityList ),
	(
		to_set( EntityList, EntitySet ) ->
		(
			inferred_{RELATIONSHIP_NAME}( EntitySet, EntityA, EntityB )
			;
			inferred_{RELATIONSHIP_NAME}( EntitySet, EntityB, EntityA)
		)
	).

check_{RELATIONSHIP_NAME}( EntityA, EntityB ) :-
	asserted_{RELATIONSHIP_NAME}( EntityList ),
	to_set( EntityList, EntitySet ),
	in_set( EntityA, EntitySet ),
	in_set( EntityB, EntitySet ),
	!.

{RELATIONSHIP_NAME}( EntityA, EntityB ) :-
	(var(EntityA) ; var(EntityB)) -> inferred_{RELATIONSHIP_NAME}( EntityA, EntityB ) ; check_{RELATIONSHIP_NAME}( EntityA, EntityB ).

%%--------------------------------------------------------------------------------------------------
