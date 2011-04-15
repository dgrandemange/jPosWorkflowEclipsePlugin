	<group name="Financial">
		
		<participant class="org.jpos.jposext.jposworkflow.sample.participant.CheckRequiredFields">
			<property name="fields" value="0,3,4,17,49,32,41,43,37,PAN,AMOUNT" />
		</participant>
		
		<participant class="org.jpos.jposext.jposworkflow.sample.participant.CheckCurrency" />
		
	</group>

