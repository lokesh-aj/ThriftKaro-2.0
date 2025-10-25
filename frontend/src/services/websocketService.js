import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { backend_url } from '../server';

class WebSocketService {
  constructor() {
    this.client = null;
    this.isConnected = false;
    this.subscriptions = new Map();
  }

  connect(userId, conversationId, onMessageReceived, onUserJoined, onUserLeft) {
    if (this.isConnected) {
      return;
    }

    // Create SockJS connection using configured backend URL
    // Add options to handle permissions policy violations
    const socket = new SockJS(`${backend_url}ws`, null, {
      transports: ['websocket', 'xhr-streaming', 'xhr-polling']
    });
    
    // Create STOMP client
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      onConnect: (frame) => {
        console.log('Connected to WebSocket:', frame);
        this.isConnected = true;
        
        // Subscribe to public messages
        const subscription = this.client.subscribe('/topic/public', (message) => {
          const chatMessage = JSON.parse(message.body);
          
          // Only process messages for the current conversation
          if (chatMessage.conversationId === conversationId) {
            if (chatMessage.type === 'JOIN') {
              onUserJoined && onUserJoined(chatMessage);
            } else if (chatMessage.type === 'LEAVE') {
              onUserLeft && onUserLeft(chatMessage);
            } else {
              onMessageReceived && onMessageReceived(chatMessage);
            }
          }
        });
        
        this.subscriptions.set('/topic/public', subscription);
        
        // Send join message
        this.sendJoinMessage(userId, conversationId);
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        this.isConnected = false;
        this.subscriptions.clear();
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
      }
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client && this.isConnected) {
      // Send leave message before disconnecting
      this.sendLeaveMessage();
      
      // Unsubscribe from all topics
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();
      
      this.client.deactivate();
      this.isConnected = false;
    }
  }

  sendMessage(chatMessage) {
    if (this.client && this.isConnected) {
      this.client.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(chatMessage)
      });
    } else {
      console.error('WebSocket not connected');
    }
  }

  sendJoinMessage(userId, conversationId) {
    if (this.client && this.isConnected) {
      const joinMessage = {
        sender: userId,
        conversationId: conversationId,
        type: 'JOIN'
      };
      
      this.client.publish({
        destination: '/app/chat.addUser',
        body: JSON.stringify(joinMessage)
      });
    }
  }

  sendLeaveMessage() {
    if (this.client && this.isConnected) {
      const leaveMessage = {
        sender: this.currentUserId,
        conversationId: this.currentConversationId,
        type: 'LEAVE'
      };
      
      this.client.publish({
        destination: '/app/chat.leave',
        body: JSON.stringify(leaveMessage)
      });
    }
  }

  isWebSocketConnected() {
    return this.isConnected;
  }
}

export default new WebSocketService();
