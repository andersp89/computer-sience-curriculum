#from twilio.rest import TwilioRestClient

#account_sid = "AC20a89902131b1c2df1e5b0d421dc88a3" # Your Account SID from www.twilio.com/console
#auth_token  = "990d57aab7a95ea41544ab204e042504"  # Your Auth Token from www.twilio.com/console

#client = Client(account_sid, auth_token)

#message = client.messages.create(to="+51580419", 
    #from_="+51580419",
    #body="Hello from Python!")

from twilio.rest import TwilioRestClient

account = "AC20a89902131b1c2df1e5b0d421dc88a3"
token = "990d57aab7a95ea41544ab204e042504"
client = TwilioRestClient(account, token)

message = client.messages.create(to="+4551580419", from_="+16502854887",
                                 body="Hello there!")


print(message.sid)
