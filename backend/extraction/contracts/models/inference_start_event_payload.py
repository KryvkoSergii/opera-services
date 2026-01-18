from __future__ import annotations
from typing import Dict
from .event_source import EventSource
class InferenceStartEventPayload: 
  def __init__(self, input: Dict):
    self._request_id: str = (input.get('request_id') or input.get('requestId'))
    self._item_id: str = (input.get('item_id') or input.get('itemId'))
    self._file_location: str = (input.get('file_location') or input.get('fileLocation'))
    self._source: EventSource = EventSource(input['source'])

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
  def file_location(self) -> str:
    return self._file_location
  @file_location.setter
  def file_location(self, file_location: str):
    self._file_location = file_location

  @property
  def source(self) -> EventSource:
    return self._source
  @source.setter
  def source(self, source: EventSource):
    self._source = source
