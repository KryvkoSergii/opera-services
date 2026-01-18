from __future__ import annotations
from typing import Dict
from .event_status import EventStatus
class InferenceResultEventPayload: 
  def __init__(self, input: Dict):
    self._request_id: str = (input.get('request_id') or input.get('requestId'))
    self._item_id: str = (input.get('item_id') or input.get('itemId'))
    self._model_name: str = input['model_name']
    if 'probability' in input:
      self._probability: float = input['probability']
    if 'status' in input:
      self._status: EventStatus = EventStatus(input['status'])
    if 'error' in input:
      self._error: str = input['error']

  @property
  def request_id(self) -> str:
    return self._request_id
  @request_id.setter
  def request_id(self, request_id: str):
    self._request_id = request_id

  @property
  def item_id(self) -> str:
    return self._item_id
  @item_id.setter
  def item_id(self, item_id: str):
    self._item_id = item_id

  @property
  def model_name(self) -> str:
    return self._model_name
  @model_name.setter
  def model_name(self, model_name: str):
    self._model_name = model_name

  @property
  def probability(self) -> float:
    return self._probability
  @probability.setter
  def probability(self, probability: float):
    self._probability = probability

  @property
  def status(self) -> EventStatus:
    return self._status
  @status.setter
  def status(self, status: EventStatus):
    self._status = status

  @property
  def error(self) -> str:
    return self._error
  @error.setter
  def error(self, error: str):
    self._error = error
