%%--------------------------------------------------------------------------------------------------

transverse_{RELATIONSHIP_NAME}( Entity, OtherEntity, CoveredSet ) :-
	asserted_{RELATIONSHIP_NAME}( Entity, Next ),
	not_in_set( Next, CoveredSet ),
	insert_to_set( Next, CoveredSet, NewCoveredSet ),
	( transverse_{RELATIONSHIP_NAME}( Next, OtherEntity, NewCoveredSet ) ; OtherEntity = Next ).

inv_transverse_{RELATIONSHIP_NAME}( OtherEntity, Entity, CoveredSet ) :-
	asserted_{RELATIONSHIP_NAME}( Prev, Entity ),
	not_in_set( Prev, CoveredSet ),
	insert_to_set( Prev, CoveredSet, NewCoveredSet ),
	( inv_transverse_{RELATIONSHIP_NAME}( OtherEntity, Prev, NewCoveredSet ) ; OtherEntity = Prev ).

{RELATIONSHIP_NAME}( EntityA, EntityB ) :-
	(var(EntityA), var(EntityB)) ->
	( 
		findall( X, asserted_{RELATIONSHIP_NAME}( X, _ ), ListOfEntityA ),
		to_set( ListOfEntityA, SetOfEntityA ),
		iterate_set( SetOfEntityA, EntityA ),
		transverse_{RELATIONSHIP_NAME}( EntityA, EntityB, [EntityA] )
	) ;
	(
		var(EntityA) ->
		( inv_transverse_{RELATIONSHIP_NAME}( EntityA, EntityB, [EntityB] ) )
		;
		( transverse_{RELATIONSHIP_NAME}( EntityA, EntityB, [EntityA] ) )
	).

%%--------------------------------------------------------------------------------------------------
